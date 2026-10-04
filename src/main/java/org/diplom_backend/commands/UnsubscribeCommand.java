package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.model.UserSubscriptionEntity;
import org.diplom_backend.services.DirectionService;
import org.diplom_backend.services.SubjectService;
import org.diplom_backend.services.SubjectTopicService;
import org.diplom_backend.services.SubscriptionService;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.diplom_backend.commands.Command.UNSUB;


@Slf4j
@RequiredArgsConstructor
@Component
public class UnsubscribeCommand implements CommandExecutor {

    private final TelegramStateService stateService;
    private final SubscriptionService subscriptionService;
    private final DirectionService courseService;
    private final SubjectService subjectService;
    private final SubjectTopicService subjectTopicService;


    @Override
    public SendMessage execute(String message, long chatId) {
        log.debug("UnsubscribeCommand received message: '{}' for chatId: {}", message, chatId);

        // Проверяем авторизацию
        if (!stateService.isUserAuthenticated(chatId)) {
            return new SendMessage(chatId,
                    "⚠️ <b>Сначала необходимо авторизоваться!</b>\n" +
                            "Используйте команду /auth для входа в систему.")
                    .parseMode(ParseMode.HTML);
        }

        // Получаем данные пользователя
        TelegramStateService.TelegramUser user = stateService.getAuthenticatedUser(chatId);
        if (user == null) {
            return new SendMessage(chatId,
                    "❌ Ошибка авторизации. Пожалуйста, войдите снова.")
                    .parseMode(ParseMode.HTML);
        }

        String trimmed = message.trim();

        // Если это команда /unsubscribe без параметров - показываем меню
        if (trimmed.equals(UNSUB.getName())) {
            return showUnsubscribeMenu(chatId, user.getAccountId());
        }

        // Обработка callback-данных
        if (isCallbackData(trimmed)) {
            return handleCallbackData(trimmed, chatId, user.getAccountId());
        }

        return new SendMessage(chatId,
                "❌ Неизвестная команда. Используйте /unsubscribe для отписки.")
                .parseMode(ParseMode.HTML);
    }

    @Override
    public String getCommandName() {
        return UNSUB.getName();
    }

    /**
     * Признак callback‑данных для отписки
     */
    private boolean isCallbackData(String data) {
        if (data == null) return false;
        return data.startsWith("unsub:")
                || "back_unsub".equals(data)
                || "cancel_unsub".equals(data)
                || data.startsWith("confirm_unsub:")
                || "confirm_unsub_all".equals(data)  // ДОБАВЛЕНО
                || "unsub_all".equals(data);
    }

    /**
     * Обработка callback-данных
     */
    private SendMessage handleCallbackData(String callbackData, long chatId, Long accountId) {
        log.debug("Handling unsubscribe callback: '{}' for chatId: {}", callbackData, chatId);

        // Отмена
        if ("cancel_unsub".equals(callbackData)) {
            return new SendMessage(chatId,
                    "✅ Отмена отписки.\n" +
                            "Ваши подписки остались без изменений.")
                    .parseMode(ParseMode.HTML);
        }

        // Подтверждение отписки от конкретной подписки
        if (callbackData.startsWith("confirm_unsub:")) {
            String[] parts = callbackData.split(":");
            if (parts.length >= 2) {
                try {
                    Long subscriptionId = Long.parseLong(parts[1]);
                    subscriptionService.unsubscribe(subscriptionId);

                    return new SendMessage(chatId,
                            "✅ Вы успешно отписались!\n\n" +
                                    "Вы больше не будете получать уведомления по этой подписке.\n\n" +
                                    "/unsubscribe — управление другими подписками")
                            .parseMode(ParseMode.HTML);
                } catch (Exception e) {
                    log.error("Error unsubscribing for chatId {}: {}", chatId, e.getMessage(), e);
                    return new SendMessage(chatId,
                            "❌ Ошибка при отписке.\n" +
                                    "Попробуйте позже.")
                            .parseMode(ParseMode.HTML);
                }
            }
        }

        // 🔥 ДОБАВЛЕНО: Отписка от всего
        if ("confirm_unsub_all".equals(callbackData)) {
            return handleUnsubscribeAll(chatId, accountId);
        }

        // Отписка от всего (подтверждение)
        if ("unsub_all".equals(callbackData)) {
            return confirmUnsubscribeAll(chatId, accountId);
        }

        // Выбор конкретной подписки для отписки
        if (callbackData.startsWith("unsub:")) {
            String[] parts = callbackData.split(":");
            if (parts.length >= 2) {
                try {
                    Long subscriptionId = Long.parseLong(parts[1]);
                    return confirmUnsubscribe(chatId, subscriptionId);
                } catch (NumberFormatException e) {
                    log.warn("Invalid subscription ID in callback: {}", callbackData);
                }
            }
        }

        return new SendMessage(chatId,
                "❌ Неизвестное действие.\n" +
                        "Используйте /unsubscribe для управления подписками.")
                .parseMode(ParseMode.HTML);
    }

    /**
     * Подтверждение отписки от конкретной подписки
     */
    private SendMessage confirmUnsubscribe(long chatId, Long subscriptionId) {
        try {
            UserSubscriptionEntity sub =
                    subscriptionService.getUserSubscriptions(
                                    stateService.getAuthenticatedUser(chatId).getAccountId()
                            ).stream()
                            .filter(s -> s.getId().equals(subscriptionId))
                            .findFirst()
                            .orElse(null);

            String details;
            if (sub != null) {
                details = getHumanReadableSubscription(sub);
            } else {
                details = "выбранной подписки";
            }

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                    .addRow(
                            new InlineKeyboardButton("✅ Да, отписаться")
                                    .callbackData("confirm_unsub:" + subscriptionId),
                            new InlineKeyboardButton("❌ Нет, оставить")
                                    .callbackData("cancel_unsub")
                    );

            return new SendMessage(chatId,
                    "⚠️ <b>Вы уверены, что хотите отписаться?</b>\n\n" +
                            "Подписка:\n" +
                            "• " + details + "\n\n" +
                            "После отписки вы больше не будете получать уведомления по этой подписке.")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error building confirmUnsubscribe text for chatId {}: {}", chatId, e.getMessage(), e);
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                    .addRow(
                            new InlineKeyboardButton("✅ Да, отписаться")
                                    .callbackData("confirm_unsub:" + subscriptionId),
                            new InlineKeyboardButton("❌ Нет, оставить")
                                    .callbackData("cancel_unsub")
                    );

            return new SendMessage(chatId,
                    "⚠️ <b>Вы уверены, что хотите отписаться?</b>\n\n" +
                            "После отписки вы больше не будете получать уведомления.")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);
        }
    }


    /**
     * Подтверждение отписки от всех подписок
     */
    private SendMessage confirmUnsubscribeAll(long chatId, Long accountId) {
        try {
            List<UserSubscriptionEntity> subscriptions =
                    subscriptionService.getUserSubscriptions(accountId);

            if (subscriptions.isEmpty()) {
                return new SendMessage(chatId,
                        "ℹ️ У вас нет активных подписок для отмены.")
                        .parseMode(ParseMode.HTML);
            }

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup()
                    .addRow(
                            new InlineKeyboardButton("✅ Да, отписаться от ВСЕГО")
                                    .callbackData("confirm_unsub_all"),
                            new InlineKeyboardButton("❌ Нет, оставить всё")
                                    .callbackData("cancel_unsub")
                    );

            int newsCount = 0, courseCount = 0, subjectCount = 0, topicCount = 0;
            for (UserSubscriptionEntity sub : subscriptions) {
                switch (sub.getType()) {
                    case NEWS -> newsCount++;
                    case DIRECTION -> courseCount++;
                    case SUBJECT -> subjectCount++;
                    case TOPIC -> topicCount++;
                }
            }

            StringBuilder messageText = new StringBuilder();
            messageText.append("⚠️ <b>ВНИМАНИЕ!</b>\n\n");
            messageText.append("Вы собираетесь отписаться от <b>")
                    .append(subscriptions.size()).append("</b> подписок:\n\n");

            if (newsCount > 0) messageText.append("• 📢 Новости: ").append(newsCount).append("\n");
            if (courseCount > 0) messageText.append("• 🎓 Курсы: ").append(courseCount).append("\n");
            if (subjectCount > 0) messageText.append("• 📚 Предметы: ").append(subjectCount).append("\n");
            if (topicCount > 0) messageText.append("• 📝 Темы: ").append(topicCount).append("\n");

            // Детальный список (первые 5 подписок)
            messageText.append("\nПодписки, от которых вы отписываетесь:\n");
            int limit = Math.min(5, subscriptions.size());
            for (int i = 0; i < limit; i++) {
                UserSubscriptionEntity sub = subscriptions.get(i);
                messageText.append("• ").append(getHumanReadableSubscription(sub)).append("\n");
            }
            if (subscriptions.size() > limit) {
                messageText.append("• ... и ещё ")
                        .append(subscriptions.size() - limit)
                        .append(" подписок\n");
            }

            messageText.append("\nПосле этого вы не будете получать никаких уведомлений.");

            return new SendMessage(chatId, messageText.toString())
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error confirming unsubscribe all for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при получении списка подписок.")
                    .parseMode(ParseMode.HTML);
        }
    }

    private String getHumanReadableSubscription(UserSubscriptionEntity subscription) {
        try {
            switch (subscription.getType()) {
                case NEWS:
                    return "📢 Новости";

                case DIRECTION: {
                    Long courseNumber = subscription.getDirectionId();
                    return "🎓 Курс " + courseNumber;
                }

                case SUBJECT: {
                    Long subjectId = subscription.getSubjectId();
                    SubjectEntity subject = subjectService.getSubject(subjectId);
                    Long courseNumber = subject.getDirection() != null
                            ? subject.getDirection().getId()
                            : subscription.getDirectionId();
                    if (courseNumber != null) {
                        return String.format("📚 Курс %d → %s",
                                courseNumber, subject.getName());
                    } else {
                        return "📚 " + subject.getName();
                    }
                }

                case TOPIC: {
                    Long topicId = subscription.getTopicId();
                    SubjectTopicEntity topic = subjectTopicService.getSubjectTopicWithRelations(topicId);
                    SubjectEntity subject = topic.getSubject();
                    Long courseNumber = subject.getDirection() != null
                            ? subject.getDirection().getId()
                            : subscription.getDirectionId();
                    String topicName = (topic.getName() != null) ? topic.getName() : "Тема";

                    if (courseNumber != null) {
                        return String.format("📝 Курс %d → %s → %s",
                                courseNumber, subject.getName(), topicName);
                    } else {
                        return String.format("📝 %s → %s",
                                subject.getName(), topicName);
                    }
                }

                default:
                    return "❓ Неизвестная подписка";
            }
        } catch (Exception e) {
            log.error("Error building human readable subscription for id {}: {}", subscription.getId(), e.getMessage(), e);
            return "❓ Подписка #" + subscription.getId();
        }
    }


    /**
     * Обработка подтверждения отписки от всего
     */
    private SendMessage handleUnsubscribeAll(long chatId, Long accountId) {
        try {
            List<UserSubscriptionEntity> subscriptions =
                    subscriptionService.getUserSubscriptions(accountId);

            int unsubscribedCount = 0;
            for (UserSubscriptionEntity subscription : subscriptions) {
                try {
                    subscriptionService.unsubscribe(subscription.getId());
                    unsubscribedCount++;
                } catch (Exception e) {
                    log.error("Error unsubscribing from {}: {}", subscription.getId(), e.getMessage());
                }
            }

            return new SendMessage(chatId,
                    String.format("✅ Вы отписались от <b>%d</b> подписок!\n\n" +
                                    "Вы больше не будете получать никаких уведомлений.\n\n" +
                                    "Если захотите снова подписаться — используйте команду /subscribe",
                            unsubscribedCount))
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error unsubscribing all for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при массовой отписке.\n" +
                            "Часть подписок могла не отмениться.")
                    .parseMode(ParseMode.HTML);
        }
    }

    /**
     * Показать меню отписки
     */
    private SendMessage showUnsubscribeMenu(long chatId, Long accountId) {
        try {
            List<UserSubscriptionEntity> subscriptions =
                    subscriptionService.getUserSubscriptions(accountId);

            if (subscriptions.isEmpty()) {
                return new SendMessage(chatId,
                        "ℹ️ У вас нет активных подписок.\n\n" +
                                "Вы можете подписаться на что-нибудь с помощью команды /subscribe.")
                        .parseMode(ParseMode.HTML);
            }

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            for (UserSubscriptionEntity subscription : subscriptions) {
                String buttonText = getSubscriptionDisplayText(subscription);
                String callbackData = "unsub:" + subscription.getId();
                keyboard.addRow(new InlineKeyboardButton(buttonText).callbackData(callbackData));
            }

            keyboard.addRow(
                    new InlineKeyboardButton("🚫 Отписаться от ВСЕГО")
                            .callbackData("unsub_all")
            );

            keyboard.addRow(
                    new InlineKeyboardButton("❌ Отмена").callbackData("cancel_unsub")
            );

            return new SendMessage(chatId,
                    "🔕 Выберите, от чего хотите отписаться:\n\n" +
                            "Вы можете отписаться от отдельных подписок или от всех сразу.")
                    .replyMarkup(keyboard)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            log.error("Error showing unsubscribe menu for chatId {}: {}", chatId, e.getMessage(), e);
            return new SendMessage(chatId,
                    "❌ Ошибка при получении списка подписок.\n" +
                            "Попробуйте позже.")
                    .parseMode(ParseMode.HTML);
        }
    }

    /**
     * Получить текстовое представление подписки
     */
    private String getSubscriptionDisplayText(UserSubscriptionEntity subscription) {
        StringBuilder text = new StringBuilder();

        try {
            UserSubscriptionEntity.SubscriptionType type = subscription.getType();

            switch (type) {
                case NEWS -> text.append("📢 Новости");
                case DIRECTION -> {
                    Long courseNumber = subscription.getDirectionId();
                    text.append("🎓 Курс ").append(courseNumber);
                }
                case SUBJECT -> {
                    Long subjectId = subscription.getSubjectId();
                    SubjectEntity subject = subjectService.getSubject(subjectId);
                    Long courseNumber = subject.getDirection() != null
                            ? subject.getDirection().getId()
                            : subscription.getDirectionId();

                    if (courseNumber != null) {
                        text.append("📚 Курс ").append(courseNumber)
                                .append(" → ").append(subject.getName());
                    } else {
                        text.append("📚 ").append(subject.getName());
                    }
                }
                case TOPIC -> {
                    Long topicId = subscription.getTopicId();
                    SubjectTopicEntity topic = subjectTopicService.getSubjectTopicWithRelations(topicId);
                    SubjectEntity subject = topic.getSubject();
                    Long courseNumber = subject.getDirection() != null
                            ? subject.getDirection().getId()
                            : subscription.getDirectionId();

                    String topicName = (topic.getName() != null) ? topic.getName() : "Тема";

                    if (courseNumber != null) {
                        text.append("📝 Курс ").append(courseNumber)
                                .append(" → ").append(subject.getName())
                                .append(" → ").append(topicName);
                    } else {
                        text.append("📝 ").append(subject.getName())
                                .append(" → ").append(topicName);
                    }
                }
                default -> text.append("❓ Неизвестная подписка");
            }
        } catch (Exception e) {
            log.error("Error building subscription text for id {}: {}", subscription.getId(), e.getMessage(), e);
            text.setLength(0);
            text.append("❓ Подписка #").append(subscription.getId());
        }

        // При желании можно оставить ID в конце для отладки:
        // text.append(" (ID: ").append(subscription.getId()).append(")");
        return text.toString();
    }

}