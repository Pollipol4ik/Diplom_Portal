package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.AlreadySubscribedException;
import org.diplom_backend.model.DirectionEntity;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.services.DirectionService;
import org.diplom_backend.services.SubjectService;
import org.diplom_backend.services.SubjectTopicService;
import org.diplom_backend.services.SubscriptionService;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.diplom_backend.commands.Command.SUB;
import static org.diplom_backend.utils.MessagesUtils.AUTH_ERROR;
import static org.diplom_backend.utils.MessagesUtils.ERROR_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Component
public class SubscribeCommand implements CommandExecutor {

    private final TelegramStateService stateService;
    private final SubscriptionService subscriptionService;
    private final DirectionService courseService;
    private final SubjectService subjectService;
    private final SubjectTopicService subjectTopicService;

    @Override
    public SendMessage execute(String message, long chatId) {
        log.debug("SubscribeCommand received message: '{}' for chatId: {}", message, chatId);

        if (message == null || message.trim().isEmpty()) {
            return new SendMessage(chatId, ERROR_MESSAGE).parseMode(ParseMode.HTML);
        }

        // Проверяем авторизацию
        if (!stateService.isUserAuthenticated(chatId)) {
            return new SendMessage(chatId,
                    "⚠️ Сначала необходимо авторизоваться!\n\n" +
                            "Используйте команду /auth для входа в систему.")
                    .parseMode(ParseMode.HTML);
        }

        String trimmed = message.trim();

        // Callback‑данные
        if (isCallbackData(trimmed)) {
            return handleCallbackData(trimmed, chatId);
        }

        // Старт команды /subscribe
        if (trimmed.equals(SUB.getName())) {
            return startSubscriptionProcess(chatId);
        }

        return new SendMessage(chatId, ERROR_MESSAGE).parseMode(ParseMode.HTML);
    }

    @Override
    public String getCommandName() {
        return SUB.getName();
    }

    /**
     * Признак callback‑данных
     */
    private boolean isCallbackData(String data) {
        if (data == null) return false;
        return data.startsWith("subtype:")
                || data.startsWith("course:")
                || data.startsWith("subject:")
                || data.startsWith("topic:")
                || data.startsWith("subact:")
                || data.startsWith("topicact:")
                || data.startsWith("finish:")
                || "back".equals(data)
                || "cancel".equals(data)
                || "ignore".equals(data);
    }

    /**
     * Общий обработчик callback‑данных
     */
    private SendMessage handleCallbackData(String callbackData, long chatId) {
        log.debug("Handling callback data: '{}' for chatId: {}", callbackData, chatId);

        TelegramStateService.UserAuthState currentState = stateService.getAuthState(chatId);

        // Общие команды
        if ("back".equals(callbackData)) {
            return handleBackCommand(chatId, currentState);
        }
        if ("cancel".equals(callbackData)) {
            return handleCancelCommand(chatId);
        }
        if ("ignore".equals(callbackData)) {
            // Просто проигнорировать и подсказать пользователю
            return new SendMessage(chatId,
                    "ℹ️ Пожалуйста, выберите один из доступных вариантов в меню.")
                    .parseMode(ParseMode.HTML);
        }
        if (callbackData.startsWith("finish:")) {
            return finishSubscription(chatId);
        }

        // По состоянию пользователя
        switch (currentState) {
            case WAITING_SUBSCRIBE_TYPE:
                return handleSubscribeTypeChoice(chatId, callbackData);
            case WAITING_COURSE_CHOICE:
                return handleCourseChoice(chatId, callbackData);
            case WAITING_SUBJECT_ACTION:
                return handleSubjectActionChoice(chatId, callbackData);
            case WAITING_TOPIC_ACTION:
                return handleTopicActionChoice(chatId, callbackData);
            case NONE:
                // Если состояние потеряно, но пришёл callback подписки — начнём заново
                if (callbackData.startsWith("subtype:")) {
                    return startSubscriptionProcess(chatId);
                }
                return new SendMessage(chatId,
                        "❌ Сессия настройки подписки истекла.\n\n" +
                                "Пожалуйста, начните заново командой /subscribe.")
                        .parseMode(ParseMode.HTML);
            default:
                return new SendMessage(chatId,
                        "❌ Невозможно обработать запрос.\n\n" +
                                "Пожалуйста, начните заново командой /subscribe.")
                        .parseMode(ParseMode.HTML);
        }
    }

    /**
     * Универсальный безопасный парсер callback‑строки формата prefix:value
     */
    private String parseCallback(String data, String expectedPrefix, long chatId) {
        if (data == null || !data.startsWith(expectedPrefix + ":")) {
            log.warn("Callback prefix mismatch. Expected '{}:', actual '{}'", expectedPrefix, data);
            return null;
        }
        int idx = data.indexOf(':');
        if (idx < 0 || idx == data.length() - 1) {
            log.warn("Callback data without value: '{}'", data);
            return null;
        }
        return data.substring(idx + 1); // часть после двоеточия
    }

    // ===== Старт процесса =====

    private SendMessage startSubscriptionProcess(long chatId) {
        log.debug("Starting subscription process for chatId: {}", chatId);

        TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
        if (user == null) {
            log.error("User not found for chatId: {}", chatId);
            return new SendMessage(chatId, AUTH_ERROR).parseMode(ParseMode.HTML);
        }

        TelegramStateService.SubscribeData subscribeData = new TelegramStateService.SubscribeData();
        stateService.saveSubscribeData(chatId, subscribeData);

        return showSubscribeTypeMenu(chatId);
    }

    private SendMessage showSubscribeTypeMenu(long chatId) {
        return showSubscribeTypeMenu(chatId,
                "📚 Выберите тип подписки:\n\n" +
                        "• 📢 Новости — общие новости и анонсы платформы\n" +
                        "• 🎓 Публикации по курсам — учебные материалы по конкретным курсам");
    }

    private SendMessage showSubscribeTypeMenu(long chatId, String text) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton("📢 Новости").callbackData("subtype:news"),
                        new InlineKeyboardButton("🎓 Публикации по курсам").callbackData("subtype:courses")
                )
                .addRow(
                        new InlineKeyboardButton("✅ Завершить настройку").callbackData("finish:all"),
                        new InlineKeyboardButton("❌ Отмена").callbackData("cancel")
                );

        stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_SUBSCRIBE_TYPE);

        return new SendMessage(chatId, text)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML);
    }

    // ===== выбор типа подписки =====

    private SendMessage handleSubscribeTypeChoice(long chatId, String callbackData) {
        String value = parseCallback(callbackData, "subtype", chatId);
        if (value == null) {
            return new SendMessage(chatId,
                    "❌ Неверный формат данных.\n\n" +
                            "Пожалуйста, выберите вариант из меню.")
                    .parseMode(ParseMode.HTML);
        }

        switch (value) {
            case "news":
                return subscribeToNews(chatId);
            case "courses":
                return showCoursesList(chatId);
            default:
                log.warn("Unknown subscription subtype: {}", value);
                return new SendMessage(chatId,
                        "❌ Неизвестный тип подписки.\n\n" +
                                "Пожалуйста, выберите вариант из меню.")
                        .parseMode(ParseMode.HTML);
        }
    }

    private SendMessage subscribeToNews(long chatId) {
        try {
            TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
            if (user == null) {
                return new SendMessage(chatId, AUTH_ERROR).parseMode(ParseMode.HTML);
            }

            subscriptionService.subscribeToNews(user.getAccountId());

            TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
            if (data == null) {
                data = new TelegramStateService.SubscribeData();
                stateService.saveSubscribeData(chatId, data);
            }
            data.setSubscribeToNews(true);

            log.info("User {} (chatId={}) subscribed to news", user.getAccountId(), chatId);

            return showSubscribeTypeMenu(chatId,
                    "✅ Вы подписались на новости!\n\n" +
                            "Теперь вы будете получать уведомления об общих новостях платформы.\n\n" +
                            "Хотите добавить другие подписки?");
        } catch (AlreadySubscribedException ex) {
            log.info("User already subscribed to news, chatId={}", chatId);
            return showSubscribeTypeMenu(chatId,
                    "ℹ️ Вы уже подписаны на новости.\n\n" +
                            "Хотите добавить другие подписки?");
        } catch (Exception e) {
            log.error("Error subscribing to news for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при оформлении подписки.\n\n" +
                            "Попробуйте ещё раз или обратитесь в поддержку.")
                    .parseMode(ParseMode.HTML);
        }
    }


    // ===== курсы =====

    private SendMessage showCoursesList(long chatId) {
        try {
            List<DirectionEntity> courses = courseService.findAll();

            if (courses.isEmpty()) {
                return new SendMessage(chatId,
                        "📭 Курсы не найдены.\n\n" +
                                "В системе пока нет доступных курсов.\n" +
                                "Обратитесь к администратору.")
                        .parseMode(ParseMode.HTML);
            }

//            courses.sort((c1, c2) -> Integer.compare(Math.toIntExact(c1.getId()), Math.toIntExact(c2.getId())));

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            for (DirectionEntity course : courses) {
                String text = "🎓 Курс " + course.getName();

                keyboard.addRow(
                        new InlineKeyboardButton(text)
                                .callbackData("course:" + course.getId())
                );
            }

            keyboard.addRow(
                    new InlineKeyboardButton("⬅️ Назад к выбору типа").callbackData("back"),
                    new InlineKeyboardButton("❌ Отмена").callbackData("cancel")
            );

            stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_COURSE_CHOICE);

            return new SendMessage(chatId,
                    "🎓 Выберите курс:\n\n" +
                            "Вы будете получать уведомления о новых публикациях в выбранном курсе.")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error fetching courses for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при получении списка курсов.\n\n" +
                            "Попробуйте ещё раз позже.")
                    .parseMode(ParseMode.HTML);
        }
    }

    private SendMessage handleCourseChoice(long chatId, String callbackData) {
        String value = parseCallback(callbackData, "course", chatId);
        if (value == null) {
            return new SendMessage(chatId,
                    "❌ Неверный формат выбора курса.\n\n" +
                            "Пожалуйста, выберите курс из списка.")
                    .parseMode(ParseMode.HTML);
        }

        try {
            Long courseNumber = Long.parseLong(value);

            TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
            if (data == null) {
                data = new TelegramStateService.SubscribeData();
                stateService.saveSubscribeData(chatId, data);
            }
            data.setCourseId(courseNumber.longValue());

            log.debug("User selected course {} (chatId={})", courseNumber, chatId);

            return showSubjectsListWithActions(chatId, courseNumber);

        } catch (NumberFormatException ex) {
            log.warn("Invalid course number in callback: {}", value);
            return new SendMessage(chatId,
                    "❌ Неверный номер курса.\n\n" +
                            "Пожалуйста, выберите курс из списка.")
                    .parseMode(ParseMode.HTML);
        } catch (Exception e) {
            log.error("Error processing course choice for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при выборе курса.\n\n" +
                            "Попробуйте выбрать другой курс.")
                    .parseMode(ParseMode.HTML);
        }
    }

    private SendMessage showSubjectsListWithActions(long chatId, Long courseNumber) {
        try {
            Page<SubjectEntity> subjects =
                    subjectService.findAllByDirectionId(0, 100, courseNumber);

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                    .addRow(
                            new InlineKeyboardButton("✅ Подписаться на ВЕСЬ КУРС " + courseNumber)
                                    .callbackData("subact:course_only")
                    );

            if (!subjects.isEmpty()) {
                keyboard.addRow(
                        new InlineKeyboardButton("────── ИЛИ ──────").callbackData("ignore")
                );

                for (SubjectEntity subject : subjects.getContent()) {
                    String name = subject.getName();
                    if (name.length() > 40) {
                        name = name.substring(0, 37) + "...";
                    }
                    keyboard.addRow(
                            new InlineKeyboardButton("📚 " + name)
                                    .callbackData("subject:" + subject.getId())
                    );
                }
            }

            keyboard.addRow(
                    new InlineKeyboardButton("⬅️ Выбрать другой курс").callbackData("back"),
                    new InlineKeyboardButton("❌ Отмена").callbackData("cancel")
            );

            stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_SUBJECT_ACTION);

            return new SendMessage(chatId,
                    String.format("📖 Курс %d — выбор предмета:\n\n", courseNumber) +
                            "Варианты подписки:\n" +
                            "1. ✅ Весь курс — уведомления по всем предметам курса\n" +
                            "2. 📚 Конкретный предмет — уведомления только по выбранному предмету")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error fetching subjects for course {} chatId {}: {}", courseNumber, chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    String.format("❌ Ошибка при получении предметов курса %d.\n\n", courseNumber) +
                            "Попробуйте выбрать другой курс.")
                    .parseMode(ParseMode.HTML);
        }
    }

    private SendMessage handleSubjectActionChoice(long chatId, String callbackData) {
        try {
            if ("subact:course_only".equals(callbackData)) {
                TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
                if (data != null && data.getCourseId() != null) {
                    TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
                    if (user != null) {
                        try {
                            subscriptionService.subscribeToCourse(
                                    user.getAccountId(),
                                    data.getCourseId()
                            );
                            data.setSubscribeToCourse(true);

                            log.info("User subscribed to entire course {}, chatId={}", data.getCourseId(), chatId);
                            return showSubscribeTypeMenu(chatId,
                                    String.format("✅ Вы подписались на курс!\n\n" +
                                                    "📚 <b>Курс %d</b>\n" +
                                                    "Теперь вы будете получать уведомления о всех новых публикациях в этом курсе.\n\n" +
                                                    "Хотите добавить другие подписки?",
                                            data.getCourseId()));
                        } catch (AlreadySubscribedException ex) {
                            log.info("User already subscribed to course {}, chatId={}", data.getCourseId(), chatId);
                            return showSubscribeTypeMenu(chatId,
                                    String.format("ℹ️ Вы уже подписаны на курс %d.\n\n" +
                                                    "Можете выбрать другой курс или предмет.",
                                            data.getCourseId().intValue()));
                        }
                    }
                }
            }
            if ("ignore".equals(callbackData)) {
                return new SendMessage(chatId,
                        "ℹ️ Пожалуйста, выберите предмет из списка.")
                        .parseMode(ParseMode.HTML);
            }

            String value = parseCallback(callbackData, "subject", chatId);
            if (value == null) {
                return new SendMessage(chatId,
                        "❌ Неверный формат выбора предмета.\n\n" +
                                "Пожалуйста, выберите предмет из списка.")
                        .parseMode(ParseMode.HTML);
            }

            Long subjectId = Long.parseLong(value);
            TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
            if (data == null) {
                data = new TelegramStateService.SubscribeData();
                stateService.saveSubscribeData(chatId, data);
            }

            data.setSubjectId(subjectId);
            log.debug("User selected subject {} (chatId={})", subjectId, chatId);
            return showTopicsListWithActions(chatId, subjectId);

        } catch (NumberFormatException ex) {
            log.warn("Invalid subject ID in callback: {}", callbackData);
            return new SendMessage(chatId,
                    "❌ Неверный формат.\n\n" +
                            "Пожалуйста, выберите предмет из списка.")
                    .parseMode(ParseMode.HTML);
        } catch (Exception e) {
            log.error("Error processing subject action for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при выборе предмета.\n\n" +
                            "Попробуйте выбрать другой предмет.")
                    .parseMode(ParseMode.HTML);
        }
    }

    private SendMessage handleTopicActionChoice(long chatId, String callbackData) {
        try {
            // Подписка на весь предмет
            if ("topicact:subject_only".equals(callbackData)) {
                TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
                if (data != null && data.getSubjectId() != null) {
                    TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
                    if (user != null) {
                        // Получаем информацию о предмете для отображения
                        SubjectEntity subject = subjectService.getSubject(data.getSubjectId());
                        Long courseNumber = subject.getDirection() != null
                                ? subject.getDirection().getId()
                                : (data.getCourseId() != null ? data.getCourseId().intValue() : null);
                        try {
                            subscriptionService.subscribeToSubject(
                                    user.getAccountId(),
                                    data.getSubjectId()
                            );
                            data.setSubscribeToSubject(true);

                            log.info("User subscribed to entire subject {}, chatId={}", data.getSubjectId(), chatId);

                            return showSubscribeTypeMenu(chatId,
                                    String.format("✅ Вы подписались на предмет!\n\n" +
                                                    "📚 Курс %d → %s\n" +
                                                    "Теперь вы будете получать уведомления о всех новых публикациях по этому предмету.\n\n" +
                                                    "Хотите добавить другие подписки?",
                                            courseNumber, subject.getName()));
                        } catch (AlreadySubscribedException ex) {
                            log.info("User already subscribed to subject {}, chatId={}", data.getSubjectId(), chatId);

                            String text;
                            text = String.format(
                                    "ℹ️ Вы уже подписаны на этот предмет:\n\n" +
                                            "📚 Курс %d → %s\n\n" +
                                            "Можете выбрать другой предмет или тему.",
                                    courseNumber, subject.getName());

                            return showSubscribeTypeMenu(chatId, text);
                        }
                    }
                }
            }

            if ("ignore".equals(callbackData)) {
                return new SendMessage(chatId,
                        "ℹ️ Пожалуйста, выберите тему из списка.")
                        .parseMode(ParseMode.HTML);
            }

            // Выбор конкретной темы
            String value = parseCallback(callbackData, "topic", chatId);
            if (value == null) {
                return new SendMessage(chatId,
                        "❌ Неверный формат выбора темы.\n\n" +
                                "Пожалуйста, выберите тему из списка.")
                        .parseMode(ParseMode.HTML);
            }

            Long topicId = Long.parseLong(value);
            TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
            if (data == null) {
                data = new TelegramStateService.SubscribeData();
                stateService.saveSubscribeData(chatId, data);
            }

            data.setTopicId(topicId);
            data.setSubscribeToTopic(true);

            TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
            if (user != null) {
                // Получаем полную информацию для отображения
                SubjectTopicEntity topic = subjectTopicService.getSubjectTopic(topicId);
                SubjectEntity subject = topic.getSubject();

                Long courseNumber = (subject.getDirection() != null)
                        ? subject.getDirection().getId()
                        : (data.getCourseId() != null ? data.getCourseId() : null);

                // Используем новое поле name
                String topicName = (topic.getName() != null) ? topic.getName() : "Тема";

                try {
                    subscriptionService.subscribeToTopic(user.getAccountId(), topicId);
                    log.info("User subscribed to topic {} (chatId={})", topicId, chatId);

                    return showSubscribeTypeMenu(chatId,
                            String.format("✅ Вы подписались на конкретную тему!\n\n" +
                                            "📚 <b>Курс %s</b> → <b>%s</b> → <b>%s</b>\n" +
                                            "Теперь вы будете получать уведомления о новых публикациях только по этой теме.\n\n" +
                                            "Хотите добавить другие подписки?",
                                    courseNumber != null ? courseNumber.toString() : "—",
                                    subject.getName(),
                                    topicName));
                } catch (AlreadySubscribedException ex) {
                    log.info("User already subscribed to topic {} (chatId={})", topicId, chatId);

                    String text = String.format("ℹ️ Вы уже подписаны на эту тему:\n\n📚 Курс %s → %s → %s\n\nМожете выбрать другую тему.",
                            courseNumber != null ? courseNumber.toString() : "—",
                            subject.getName(),
                            topicName);

                    return showSubscribeTypeMenu(chatId, text);
                }
            }

            return showSubscribeTypeMenu(chatId,
                    "✅ Вы подписались на конкретную тему!\n\n" +
                            "Теперь вы будете получать уведомления о публикациях по этой теме.\n\n" +
                            "Хотите добавить другие подписки?");

        } catch (NumberFormatException ex) {
            log.warn("Invalid topic ID in callback: {}", callbackData);
            return new SendMessage(chatId,
                    "❌ Неверный формат.\n\n" +
                            "Пожалуйста, выберите тему из списка.")
                    .parseMode(ParseMode.HTML);
        } catch (Exception e) {
            log.error("Error processing topic action for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при выборе темы.\n\n" +
                            "Попробуйте выбрать другую тему.")
                    .parseMode(ParseMode.HTML);
        }
    }



    // ===== темы =====

    private SendMessage showTopicsListWithActions(long chatId, Long subjectId) {
        try {
            Page<SubjectTopicEntity> topics =
                    subjectTopicService.findAllBySubjectId(0, 100, subjectId);

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                    .addRow(
                            new InlineKeyboardButton("✅ Подписаться на ВЕСЬ ПРЕДМЕТ")
                                    .callbackData("topicact:subject_only")
                    );

            if (!topics.isEmpty()) {
                keyboard.addRow(
                        new InlineKeyboardButton("────── ИЛИ ──────").callbackData("ignore")
                );

                for (SubjectTopicEntity topic : topics.getContent()) {
                    // Прямое получение имени
                    String name = (topic.getName() != null) ? topic.getName() : "Тема #" + topic.getId();

                    if (name.length() > 40) {
                        name = name.substring(0, 37) + "...";
                    }

                    keyboard.addRow(
                            new InlineKeyboardButton("📝 " + name)
                                    .callbackData("topic:" + topic.getId())
                    );
                }
            }

            keyboard.addRow(
                    new InlineKeyboardButton("⬅️ Выбрать другой предмет").callbackData("back"),
                    new InlineKeyboardButton("❌ Отмена").callbackData("cancel")
            );

            stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_TOPIC_ACTION);

            return new SendMessage(chatId,
                    "📝 Выбор темы:\n\n" +
                            "Варианты подписки:\n" +
                            "1. ✅ Весь предмет — уведомления по всем темам предмета\n" +
                            "2. 📝 Конкретная тема — уведомления только по выбранной теме")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error fetching topics for subject {} chatId {}: {}", subjectId, chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при получении списка тем.\n\n" +
                            "Попробуйте выбрать другой предмет.")
                    .parseMode(ParseMode.HTML);
        }
    }


    // ===== back / cancel / finish =====

    private SendMessage handleBackCommand(long chatId, TelegramStateService.UserAuthState currentState) {
        switch (currentState) {
            case WAITING_COURSE_CHOICE:
                return showSubscribeTypeMenu(chatId);
            case WAITING_SUBJECT_ACTION:
                return showCoursesList(chatId);
            case WAITING_TOPIC_ACTION:
                TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
                if (data != null && data.getCourseId() != null) {
                    return showSubjectsListWithActions(chatId, data.getCourseId());
                }
                return showCoursesList(chatId);
            default:
                return showSubscribeTypeMenu(chatId);
        }
    }

    private SendMessage handleCancelCommand(long chatId) {
        stateService.clearAuthState(chatId);
        stateService.clearSubscribeData(chatId);

        return new SendMessage(chatId,
                "❌ Настройка подписок отменена.\n\n" +
                        "Вы можете начать заново командой /subscribe.")
                .parseMode(ParseMode.HTML);
    }

    private SendMessage finishSubscription(long chatId) {
        try {
            TelegramStateService.SubscribeData data = stateService.getSubscribeData(chatId);
            StringBuilder msg = new StringBuilder("🎉 Подписки успешно оформлены!\n\n");

            if (data != null && hasAnySubscription(data)) {
                msg.append("Ваши активные подписки:\n\n");

                if (data.isSubscribeToNews()) {
                    msg.append("📢 Новости\n");
                    msg.append("   └─ Общие анонсы и новости платформы\n\n");
                }

                // Курс / предмет / тема с иерархией
                if (data.getCourseId() != null ||
                        data.getSubjectId() != null ||
                        data.getTopicId() != null) {

                    // 1. Курс
                    Long courseNumber = null;
                    String courseTitle = null;
                    if (data.getCourseId() != null) {
                        try {
                            DirectionEntity course = courseService.getDirectionById(data.getCourseId());
                            courseNumber = course.getId();
                            courseTitle = "Курс " + courseNumber;
                        } catch (Exception ignored) {
                        }
                    }

                    // 2. Предмет
                    SubjectEntity subject = null;
                    if (data.getSubjectId() != null) {
                        try {
                            subject = subjectService.getSubject(data.getSubjectId());
                            if (subject.getDirection() != null) {
                                courseNumber = subject.getDirection().getId();
                                courseTitle = "Курс " + courseNumber;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    // 3. Тема
                    // 3. Тема
                    SubjectTopicEntity topic = null;
                    String topicName = null;
                    if (data.getTopicId() != null) {
                        try {
                            topic = subjectTopicService.getSubjectTopicWithRelations(data.getTopicId());
                            subject = topic.getSubject();
                            if (subject != null && subject.getDirection() != null) {
                                courseNumber = subject.getDirection().getId();
                                courseTitle = "Курс " + courseNumber;
                            }
                            // Используем поле name
                            topicName = topic.getName();
                        } catch (Exception ignored) {
                        }
                    }

                    // Формирование текста
                    if (data.isSubscribeToCourse() && courseTitle != null) {
                        msg.append("✅ ").append(courseTitle).append("\n");
                        if (data.isSubscribeToSubject() && subject != null) {
                            msg.append("   └─ ").append(subject.getName()).append("\n");
                            if (data.isSubscribeToTopic() && topicName != null) {
                                msg.append("       └─ ").append(topicName).append("\n");
                            } else if (data.isSubscribeToTopic()) {
                                msg.append("       └─ Конкретная тема\n");
                            }
                        } else if (data.isSubscribeToSubject()) {
                            msg.append("   └─ Предмет (все темы)\n");
                        } else {
                            msg.append("   └─ Все предметы курса\n");
                        }
                        msg.append("\n");
                    } else if (data.isSubscribeToSubject() && subject != null) {
                        // Подписка только на предмет/тему без флага курса
                        msg.append("✅ ");
                        if (courseTitle != null) {
                            msg.append(courseTitle).append(" → ");
                        }
                        msg.append(subject.getName()).append("\n");
                        if (data.isSubscribeToTopic() && topicName != null) {
                            msg.append("   └─ ").append(topicName).append("\n");
                        } else if (data.isSubscribeToTopic()) {
                            msg.append("   └─ Конкретная тема\n");
                        } else {
                            msg.append("   └─ Все темы предмета\n");
                        }
                        msg.append("\n");
                    } else if (data.isSubscribeToTopic() && topicName != null) {
                        msg.append("✅ ");
                        if (courseTitle != null) {
                            msg.append(courseTitle).append(" → ");
                        }
                        if (subject != null) {
                            msg.append(subject.getName()).append(" → ");
                        }
                        msg.append(topicName).append("\n\n");
                    }
                }

                msg.append("📬 Вы будете получать уведомления о новых публикациях по выбранным подпискам.");
            } else {
                msg.append("ℹ️ Вы не выбрали ни одной подписки.\n\n");
                msg.append("Вы всегда можете добавить подписки позже.");
            }

            msg.append("\n\n📋 Управление подписками:\n");
            msg.append("/subscribe — добавить новые подписки\n");
            msg.append("/unsubscribe — отписаться от выбранного");

            log.info("Subscription completed for chatId={}, data={}", chatId, data);

            stateService.clearAuthState(chatId);
            stateService.clearSubscribeData(chatId);

            return new SendMessage(chatId, msg.toString()).parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error finishing subscription for chatId {}: {}", chatId, e.getMessage(), e);
            stateService.clearAuthState(chatId);
            stateService.clearSubscribeData(chatId);

            return new SendMessage(chatId,
                    "❌ Ошибка при оформлении подписки.\n\n" +
                            "Попробуйте ещё раз командой /subscribe.")
                    .parseMode(ParseMode.HTML);
        }
    }


    private boolean hasAnySubscription(TelegramStateService.SubscribeData data) {
        return (data.isSubscribeToNews())
                || (data.isSubscribeToCourse() && data.getCourseId() != null)
                || (data.isSubscribeToSubject() && data.getSubjectId() != null)
                || (data.isSubscribeToTopic() && data.getTopicId() != null);
    }
}
