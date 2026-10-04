package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.utils.MessagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.model.TelegramAccountEntity;
import org.diplom_backend.model.UserSubscriptionEntity;
import org.diplom_backend.repositories.CourseModeratorRepository;
import org.diplom_backend.repositories.TelegramAccountRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для отправки уведомлений через Telegram
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final TelegramBotService telegramBotService;
    private final SubscriptionService subscriptionService;
    private final TelegramAccountRepository telegramAccountRepository;
    private final SubjectService subjectService;
    private final SubjectTopicService subjectTopicService;
    private final CourseModeratorRepository courseModeratorRepository;

    /**
     * Асинхронно уведомить подписчиков курса
     */
    public void notifyCourseSubscribersAsync(Long courseNumber, String title, String message) {
        try {
            List<UserSubscriptionEntity> subscribers =
                    subscriptionService.getCourseSubscribers(courseNumber);

            if (subscribers.isEmpty()) {
                log.warn("Нет подписчиков на курс {} для асинхронной отправки", courseNumber);
                return;
            }

            List<Long> chatIds = getChatIdsFromSubscribers(subscribers);
            if (chatIds.isEmpty()) {
                log.warn("Не найдены Telegram аккаунты для подписчиков курса {}", courseNumber);
                return;
            }

            String formattedMessage = String.format(
                    "🎓 Курс %d\n\n%s\n\n%s",
                    courseNumber,
                    escapeHtml(title),
                    escapeHtml(message)
            );

            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
            log.info("Запущена асинхронная отправка уведомления о курсе {} подписчикам", courseNumber);
        } catch (Exception e) {
            log.error("Ошибка при асинхронной отправке уведомлений курса {}: {}",
                    courseNumber, e.getMessage(), e);
        }
    }

    /**
     * Асинхронно уведомить подписчиков предмета
     */
    // Иерархическая логика для предметов
    public void notifySubjectSubscribersAsync(Long subjectId, String title, String message) {
        try {
            Set<Long> accountIds = new HashSet<>();

            // 1. Подписчики ТОЛЬКО на этот предмет (SubscriptionType.SUBJECT)
            subscriptionService.getSubjectSubscribers(subjectId)
                    .forEach(sub -> accountIds.add(sub.getAccount().getId()));

            // 2. + Подписчики на курс (SubscriptionType.COURSE)
            Long courseNumber = getCourseNumberBySubjectId(subjectId);
            if (courseNumber != null) {
                subscriptionService.getCourseSubscribers(courseNumber)
                        .forEach(sub -> accountIds.add(sub.getAccount().getId()));
            }

            sendNotificationToAccounts(accountIds, "📚 Предмет", title, message, subjectId);

        } catch (Exception e) {
            log.error("Ошибка уведомления по предмету {}", subjectId, e);
        }
    }

    /**
     * Асинхронно уведомить подписчиков темы
     */
    // Иерархическая логика для тем
    public void notifyTopicSubscribersAsync(Long topicId, String title, String message) {
        try {
            Set<Long> accountIds = new HashSet<>();

            // 1. Подписчики ТОЛЬКО на эту тему (SubscriptionType.TOPIC)
            subscriptionService.getTopicSubscribers(topicId)
                    .forEach(sub -> accountIds.add(sub.getAccount().getId()));

            // 2. + Подписчики на предмет
            Long subjectId = getSubjectIdByTopicId(topicId);
            if (subjectId != null) {
                subscriptionService.getSubjectSubscribers(subjectId)
                        .forEach(sub -> accountIds.add(sub.getAccount().getId()));
            }

            // 3. + Подписчики на курс
            Long courseNumber = getCourseNumberByTopicId(topicId);
            if (courseNumber != null) {
                subscriptionService.getCourseSubscribers(courseNumber)
                        .forEach(sub -> accountIds.add(sub.getAccount().getId()));
            }

            sendNotificationToAccounts(accountIds, "📖 Тема", title, message, topicId);

        } catch (Exception e) {
            log.error("Ошибка уведомления по теме {}", topicId, e);
        }
    }


    private Long getCourseNumberBySubjectId(Long subjectId) {
        try {
            SubjectEntity subject = subjectService.getSubject(subjectId);
            return subject.getDirection() != null ? subject.getDirection().getId() : null;
        } catch (Exception e) {
            log.error("Не удалось найти направление для предмета {}", subjectId, e);
            return null;
        }
    }

    private Long getSubjectIdByTopicId(Long topicId) {
        try {
            SubjectTopicEntity subjectTopic = subjectTopicService.getSubjectTopicWithRelations(topicId);
            return subjectTopic.getSubject() != null ? subjectTopic.getSubject().getId() : null;
        } catch (Exception e) {
            log.error("Не удалось найти предмет для темы {}", topicId, e);
            return null;
        }
    }

    private Long getCourseNumberByTopicId(Long topicId) {
        try {
            SubjectTopicEntity subjectTopic = subjectTopicService.getSubjectTopicWithRelations(topicId);
            if (subjectTopic.getSubject() != null && subjectTopic.getSubject().getDirection() != null) {
                return subjectTopic.getSubject().getDirection().getId();
            }
            return null;
        } catch (Exception e) {
            log.error("Не удалось найти направление для темы {}", topicId, e);
            return null;
        }
    }

    private void sendNotificationToAccounts(Set<Long> accountIds, String category, String title, String message, Object entityId) {
        if (accountIds.isEmpty()) {
            log.warn("Нет подписчиков для {} {}", category, entityId);
            return;
        }

        List<Long> chatIds = getChatIdsFromAccountIds(accountIds);
        if (chatIds.isEmpty()) {
            log.warn("Нет Telegram аккаунтов для {} {}", category, entityId);
            return;
        }

        String formattedMessage = String.format("%s\n<b>%s</b>\n%s",
                category, escapeHtml(title), escapeHtml(message));

        telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        log.info("Уведомление по {}(ID={}) отправлено {} чатам", category, entityId, chatIds.size());
    }

    /**
     * Уведомить о новой публикации с учетом иерархии Курс -> Предмет -> Топик
     *
     * @param courseNumber   номер курса
     * @param subjectId      ID предмета
     * @param topicId        ID темы
     * @param subjectName    название предмета
     * @param topicName      название темы
     * @param title          заголовок публикации
     * @param description    описание публикации
     * @param authorNickname никнейм автора
     */
    public void notifyPublicationSubscribers(Long courseNumber,
                                             Long subjectId,
                                             Long topicId,
                                             String subjectName,
                                             String topicName,
                                             String title,
                                             String description,
                                             String authorNickname) {
        try {
            Set<Long> notifiedAccountIds = new HashSet<>();

            // 1. Собираем подписчиков конкретной темы (если тема указана)
            if (topicId != null) {
                List<UserSubscriptionEntity> topicSubscribers =
                        subscriptionService.getTopicSubscribers(topicId);

                topicSubscribers.stream()
                        .filter(sub -> Objects.equals(sub.getDirectionId(), courseNumber)
                                && Objects.equals(sub.getSubjectId(), subjectId)
                                && Objects.equals(sub.getTopicId(), topicId))
                        .forEach(sub -> {
                            notifiedAccountIds.add(sub.getAccount().getId());
                            log.debug("Добавлен подписчик темы: accountId={}", sub.getAccount().getId());
                        });
            }

            // 2. Собираем подписчиков предмета
            if (subjectId != null) {
                List<UserSubscriptionEntity> subjectSubscribers =
                        subscriptionService.getSubjectSubscribers(subjectId);

                subjectSubscribers.stream()
                        .filter(sub -> Objects.equals(sub.getDirectionId(), courseNumber)
                                && Objects.equals(sub.getSubjectId(), subjectId))
                        .forEach(sub -> {
                            Long accountId = sub.getAccount().getId();
                            if (notifiedAccountIds.add(accountId)) {
                                log.debug("Добавлен подписчик предмета: accountId={}", accountId);
                            }
                        });
            }

            // 3. Собираем подписчиков всего курса
            if (courseNumber != null) {
                List<UserSubscriptionEntity> courseSubscribers =
                        subscriptionService.getCourseSubscribers(courseNumber);

                courseSubscribers.forEach(sub -> {
                    Long accountId = sub.getAccount().getId();
                    if (notifiedAccountIds.add(accountId)) {
                        log.debug("Добавлен подписчик курса: accountId={}", accountId);
                    }
                });
            }

            if (notifiedAccountIds.isEmpty()) {
                log.warn("Нет подписчиков для публикации (курс {}, предмет '{}', тема '{}')",
                        courseNumber, subjectName, topicName);
                return;
            }

            // Получаем chatId для всех уникальных аккаунтов
            List<Long> chatIds = getChatIdsFromAccountIds(notifiedAccountIds);

            if (chatIds.isEmpty()) {
                log.warn("Не найдены Telegram аккаунты для подписчиков публикации");
                return;
            }

            StringBuilder messageText = new StringBuilder();
            messageText.append("🎓 <b>Новая публикация</b>\n\n");
            messageText.append("📚 <b>Курс ").append(courseNumber).append("</b>");

            if (subjectName != null && !subjectName.isEmpty()) {
                messageText.append(" → <b>").append(subjectName).append("</b>");
            }

            if (topicName != null && !topicName.isEmpty()) {
                messageText.append(" → <b>").append(topicName).append("</b>");
            }

            messageText.append("\n\n");
            messageText.append("📝 <b>").append(title).append("</b>\n\n");

            if (description != null && !description.isEmpty()) {
                String shortDescription = description.length() > 200
                        ? description.substring(0, 197) + "..."
                        : description;
                messageText.append(shortDescription).append("\n\n");
            }

            if (authorNickname != null && !authorNickname.isEmpty()) {
                messageText.append("👤 <i>Автор: ").append(authorNickname).append("</i>");
            }

            int sentCount = telegramBotService.sendBulkMessages(chatIds, messageText.toString());

            log.info("Уведомление о публикации '{}' отправлено {}/{} уникальным подписчикам " +
                            "(курс {}, предмет '{}', тема '{}')",
                    title, sentCount, chatIds.size(), courseNumber, subjectName, topicName);

        } catch (Exception e) {
            log.error("Ошибка при отправке уведомлений о публикации '{}': {}",
                    title, e.getMessage(), e);
            throw new RuntimeException("Ошибка при отправке уведомлений", e);
        }
    }

    /**
     * Получает список chatId из accountIds
     */
    private List<Long> getChatIdsFromAccountIds(Set<Long> accountIds) {
        return accountIds.stream()
                .flatMap(accountId -> telegramAccountRepository.findByAccountId(accountId).stream())
                .map(TelegramAccountEntity::getChatId)
                .distinct()
                .collect(Collectors.toList());
    }


    /**
     * Асинхронно отправить уведомление подписчикам новостей
     */
    public void notifyNewsSubscribersAsync(String title, String message) {
        try {
            List<UserSubscriptionEntity> subscribers = subscriptionService.getNewsSubscribers();

            if (subscribers.isEmpty()) {
                log.warn("Нет подписчиков на новости для асинхронной отправки");
                return;
            }

            List<Long> chatIds = getChatIdsFromSubscribers(subscribers);

            if (chatIds.isEmpty()) {
                log.warn("Не найдены Telegram аккаунты для подписчиков новостей");
                return;
            }

            String formattedMessage = String.format(
                    "📢 <b>Новая новость!</b>\n\n<b>%s</b>\n\n%s",
                    escapeHtml(title),
                    escapeHtml(message)
            );

            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);

            log.info("Запущена асинхронная отправка уведомления о новости {} подписчикам",
                    chatIds.size());

        } catch (Exception e) {
            log.error("Ошибка при асинхронной отправке уведомлений подписчикам новостей: {}",
                    e.getMessage(), e);
        }
    }


    /**
     * Получает список chatId из подписчиков
     */
    private List<Long> getChatIdsFromSubscribers(List<UserSubscriptionEntity> subscribers) {
        Set<Long> accountIds = subscribers.stream()
                .map(s -> s.getAccount().getId())
                .collect(Collectors.toSet());

        return getChatIdsFromAccountIds(accountIds);
    }


    /**
     * Экранирует специальные символы HTML
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /**
     * Уведомить участников группы о проверке слушания
     */
    public void notifyGroupMembersAboutReview(List<Long> accountIds, String groupTitle, String status, String comment, Integer grade) {
        try {
            List<Long> chatIds = getChatIdsFromAccountIds(new HashSet<>(accountIds));

            if (chatIds.isEmpty()) {
                log.debug("Участники группы не привязали Telegram, уведомление не отправлено");
                return;
            }

            String formattedMessage = String.format(
                    "📝 <b>Ваша работа проверена!</b>\n\n" +
                            "Группа: <b>%s</b>\n" +
                            "Статус: <b>%s</b>\n" +
                            "Оценка: <b>%s/10</b>\n" +
                            "Комментарий: <i>%s</i>",
                    escapeHtml(groupTitle),
                    status,
                    grade != null ? grade.toString() : "—",
                    escapeHtml(comment != null ? comment : "Без комментария")
            );

            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка при рассылке уведомлений о проверке: {}", e.getMessage());
        }
    }

    /**
     * Уведомить участников группы о комментарии модератора по слушанию без выставления оценки/статуса.
     */
    public void notifyGroupMembersAboutHearingComment(List<Long> accountIds, String groupTitle, String stageTitle, String comment) {
        try {
            List<Long> chatIds = getChatIdsFromAccountIds(new HashSet<>(accountIds));
            if (chatIds.isEmpty()) return;

            String formattedMessage = String.format(
                    "💬 <b>Новый комментарий</b>\n\n" +
                            "Этап: <b>%s</b>\n" +
                            "Группа: <b>%s</b>\n\n" +
                            "%s",
                    escapeHtml(stageTitle),
                    escapeHtml(groupTitle),
                    escapeHtml(comment != null ? comment : "")
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка при рассылке уведомлений о комментарии по слушанию: {}", e.getMessage(), e);
        }
    }

    /** Уведомить одного ученика об изменении статуса/оценке/комментарии по уроку. */
    public void notifyStudentAboutLessonReview(Long studentAccountId,
                                               String courseName,
                                               String lessonTitle,
                                               String status,
                                               Integer score,
                                               String reviewerComment) {
        try {
            List<Long> chatIds = getChatIdsFromAccountIds(Set.of(studentAccountId));
            if (chatIds.isEmpty()) return;
            String statusRu = MessagesUtils.formatSubmissionStatus(status);
            String formattedMessage = String.format(
                    "📋 <b>Результат проверки работы</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Урок: <b>%s</b>\n" +
                            "Статус: <b>%s</b>\n" +
                            "Балл: <b>%s</b>\n\n" +
                            "💬 <i>%s</i>",
                    escapeHtml(courseName),
                    escapeHtml(lessonTitle),
                    escapeHtml(statusRu),
                    score != null ? score.toString() : "—",
                    escapeHtml(reviewerComment != null ? reviewerComment : "Комментарий не оставлен")
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления ученика о проверке урока: {}", e.getMessage(), e);
        }
    }

    /** Уведомить одного ученика о комментарии модератора без изменения оценки. */
    public void notifyStudentAboutLessonComment(Long studentAccountId,
                                                String courseName,
                                                String lessonTitle,
                                                String comment) {
        try {
            List<Long> chatIds = getChatIdsFromAccountIds(Set.of(studentAccountId));
            if (chatIds.isEmpty()) return;
            String formattedMessage = String.format(
                    "💬 <b>Новый комментарий</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Урок: <b>%s</b>\n\n" +
                            "%s",
                    escapeHtml(courseName),
                    escapeHtml(lessonTitle),
                    escapeHtml(comment != null ? comment : "")
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления ученика о комментарии по уроку: {}", e.getMessage(), e);
        }
    }

    /** Уведомить модераторов курса о новой/обновлённой работе по уроку. */
    @Async
    public void notifyModeratorsAboutLessonSubmission(Long courseId,
                                                      String courseName,
                                                      String lessonTitle,
                                                      String studentNickname) {
        notifyModeratorsAboutLessonSubmission(courseId, courseName, lessonTitle, studentNickname, null, null, null, null);
    }

    /** Уведомить модераторов курса о новой/обновлённой работе по уроку (с ФИО/школой/классом). */
    @Async
    public void notifyModeratorsAboutLessonSubmission(Long courseId,
                                                      String courseName,
                                                      String lessonTitle,
                                                      String studentNickname,
                                                      String studentFullName,
                                                      String schoolName,
                                                      String className,
                                                      Long studentId) {
        try {
            Set<Long> moderatorAccountIds = courseModeratorRepository.findAllByCourse_Id(courseId).stream()
                    .map(cm -> cm.getAccount().getId())
                    .collect(Collectors.toSet());
            if (moderatorAccountIds.isEmpty()) return;
            List<Long> chatIds = getChatIdsFromAccountIds(moderatorAccountIds);
            if (chatIds.isEmpty()) return;
            String who = studentFullName != null && !studentFullName.isBlank()
                    ? studentFullName
                    : ("@" + (studentNickname != null ? studentNickname : ""));
            String extra = "";
            if ((schoolName != null && !schoolName.isBlank()) || (className != null && !className.isBlank())) {
                extra = String.format("\nШкола: <b>%s</b>\nКласс: <b>%s</b>",
                        escapeHtml(schoolName != null ? schoolName : "—"),
                        escapeHtml(className != null ? className : "—"));
            }
            String formattedMessage = String.format(
                    "📥 <b>Ученик сдал работу на проверку</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Задание: <b>%s</b>\n" +
                            "Ученик: <b>%s</b>%s\n\n" +
                            "Перейдите на платформу для проверки работы.",
                    escapeHtml(courseName),
                    escapeHtml(lessonTitle),
                    escapeHtml(who),
                    extra
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления модераторов о новой работе по уроку: {}", e.getMessage(), e);
        }
    }

    /** Уведомить модераторов курса о новом комментарии ученика по уроку. */
    @Async
    public void notifyModeratorsAboutLessonComment(Long courseId,
                                                   String courseName,
                                                   String lessonTitle,
                                                   String studentNickname,
                                                   String comment) {
        try {
            Set<Long> moderatorAccountIds = courseModeratorRepository.findAllByCourse_Id(courseId).stream()
                    .map(cm -> cm.getAccount().getId())
                    .collect(Collectors.toSet());
            if (moderatorAccountIds.isEmpty()) return;
            List<Long> chatIds = getChatIdsFromAccountIds(moderatorAccountIds);
            if (chatIds.isEmpty()) return;
            String formattedMessage = String.format(
                    "💬 <b>Комментарий ученика</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Урок: <b>%s</b>\n" +
                            "Ученик: <b>@%s</b>\n\n" +
                            "%s",
                    escapeHtml(courseName),
                    escapeHtml(lessonTitle),
                    escapeHtml(studentNickname),
                    escapeHtml(comment != null ? comment : "")
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления модераторов о комментарии ученика по уроку: {}", e.getMessage(), e);
        }
    }

    /** Уведомить модераторов курса о новой работе по слушанию. */
    @Async
    public void notifyModeratorsAboutHearingSubmission(Long courseId,
                                                       String courseName,
                                                       String stageTitle,
                                                       String groupTitle) {
        notifyModeratorsAboutHearingSubmission(courseId, courseName, stageTitle, groupTitle, null, null, null);
    }

    /** Уведомить модераторов курса о новой работе по слушанию (с ФИО/школой/классом). */
    @Async
    public void notifyModeratorsAboutHearingSubmission(Long courseId,
                                                       String courseName,
                                                       String stageTitle,
                                                       String groupTitle,
                                                       String ownerFullName,
                                                       String schoolName,
                                                       String className) {
        try {
            Set<Long> moderatorAccountIds = courseModeratorRepository.findAllByCourse_Id(courseId).stream()
                    .map(cm -> cm.getAccount().getId())
                    .collect(Collectors.toSet());
            if (moderatorAccountIds.isEmpty()) return;
            List<Long> chatIds = getChatIdsFromAccountIds(moderatorAccountIds);
            if (chatIds.isEmpty()) return;
            String extra = "";
            if ((ownerFullName != null && !ownerFullName.isBlank())
                    || (schoolName != null && !schoolName.isBlank())
                    || (className != null && !className.isBlank())) {
                extra = String.format("\nУченик: <b>%s</b>\nШкола: <b>%s</b>\nКласс: <b>%s</b>",
                        escapeHtml(ownerFullName != null ? ownerFullName : "—"),
                        escapeHtml(schoolName != null ? schoolName : "—"),
                        escapeHtml(className != null ? className : "—"));
            }
            String formattedMessage = String.format(
                    "📩 <b>Группа подала работу на слушание</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Этап: <b>%s</b>\n" +
                            "Тема группы: <b>%s</b>%s\n\n" +
                            "Перейдите на платформу для рассмотрения темы.",
                    escapeHtml(courseName),
                    escapeHtml(stageTitle),
                    escapeHtml(groupTitle),
                    extra
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления модераторов о работе на слушании: {}", e.getMessage(), e);
        }
    }

    /** Уведомить модераторов курса о комментарии ученика по слушанию. */
    @Async
    public void notifyModeratorsAboutHearingComment(Long courseId,
                                                    String courseName,
                                                    String stageTitle,
                                                    String groupTitle,
                                                    String authorNickname,
                                                    String comment) {
        try {
            Set<Long> moderatorAccountIds = courseModeratorRepository.findAllByCourse_Id(courseId).stream()
                    .map(cm -> cm.getAccount().getId())
                    .collect(Collectors.toSet());
            if (moderatorAccountIds.isEmpty()) return;
            List<Long> chatIds = getChatIdsFromAccountIds(moderatorAccountIds);
            if (chatIds.isEmpty()) return;
            String formattedMessage = String.format(
                    "💬 <b>Комментарий по слушанию</b>\n\n" +
                            "Курс: <b>%s</b>\n" +
                            "Этап: <b>%s</b>\n" +
                            "Группа: <b>%s</b>\n" +
                            "Автор: <b>@%s</b>\n\n" +
                            "%s",
                    escapeHtml(courseName),
                    escapeHtml(stageTitle),
                    escapeHtml(groupTitle),
                    escapeHtml(authorNickname),
                    escapeHtml(comment != null ? comment : "")
            );
            telegramBotService.sendBulkMessagesAsync(chatIds, formattedMessage);
        } catch (Exception e) {
            log.error("Ошибка уведомления модераторов о комментарии по слушанию: {}", e.getMessage(), e);
        }
    }
}