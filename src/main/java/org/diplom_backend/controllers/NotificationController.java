package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.dto.requests.NotificationRequestDTO;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.TelegramNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для асинхронной отправки уведомлений
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "API для асинхронной отправки уведомлений")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final TelegramNotificationService notificationService;

    /**
     * Асинхронно отправить уведомление подписчикам новостей
     */
    @PostMapping("/news")
    @IsModerator
    @Operation(summary = "Отправить уведомление о новости",
            description = "Асинхронно отправляет уведомление всем подписчикам новостей")
    public ResponseEntity<String> sendNewsNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        try {
            notificationService.notifyNewsSubscribersAsync(
                    request.getTitle(),
                    request.getMessage()
            );

            log.info("News notification queued for async sending");
            return ResponseEntity.accepted()
                    .body("Уведомление принято в обработку и будет отправлено подписчикам новостей");

        } catch (Exception e) {
            log.error("Error queuing news notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при постановке уведомления в очередь");
        }
    }

    /**
     * Асинхронно отправить уведомление подписчикам курса
     */
    @PostMapping("/course/{courseNumber}")
    @IsAdmin
    @IsModerator
    @Operation(summary = "Отправить уведомление подписчикам курса",
            description = "Асинхронно отправляет уведомление всем подписчикам указанного курса")
    public ResponseEntity<String> sendCourseNotification(
            @PathVariable Long courseNumber,
            @Valid @RequestBody NotificationRequestDTO request) {
        try {
            notificationService.notifyCourseSubscribersAsync(
                    courseNumber,
                    request.getTitle(),
                    request.getMessage()
            );

            log.info("Course notification queued for async sending for course {}", courseNumber);
            return ResponseEntity.accepted()
                    .body(String.format("Уведомление принято в обработку и будет отправлено подписчикам курса %d", courseNumber));

        } catch (Exception e) {
            log.error("Error queuing course notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при постановке уведомления в очередь");
        }
    }

    /**
     * Асинхронно отправить уведомление подписчикам предмета
     */
    @PostMapping("/subject/{subjectId}")
    @IsAdmin
    @IsModerator
    @Operation(summary = "Отправить уведомление подписчикам предмета",
            description = "Асинхронно отправляет уведомление всем подписчикам указанного предмета")
    public ResponseEntity<String> sendSubjectNotification(
            @PathVariable Long subjectId,
            @Valid @RequestBody NotificationRequestDTO request) {
        try {
            notificationService.notifySubjectSubscribersAsync(
                    subjectId,
                    request.getTitle(),
                    request.getMessage()
            );

            log.info("Subject notification queued for async sending for subject {}", subjectId);
            return ResponseEntity.accepted()
                    .body(String.format("Уведомление принято в обработку и будет отправлено подписчикам предмета %d", subjectId));

        } catch (Exception e) {
            log.error("Error queuing subject notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при постановке уведомления в очередь");
        }
    }

    /**
     * Асинхронно отправить уведомление подписчикам темы
     */
    @PostMapping("/topic/{topicId}")
    @IsAdmin
    @IsModerator
    @Operation(summary = "Отправить уведомление подписчикам темы",
            description = "Асинхронно отправляет уведомление всем подписчикам указанной темы")
    public ResponseEntity<String> sendTopicNotification(
            @PathVariable Long topicId,
            @Valid @RequestBody NotificationRequestDTO request) {
        try {
            notificationService.notifyTopicSubscribersAsync(
                    topicId,
                    request.getTitle(),
                    request.getMessage()
            );

            log.info("Topic notification queued for async sending for topic {}", topicId);
            return ResponseEntity.accepted()
                    .body(String.format("Уведомление принято в обработку и будет отправлено подписчикам темы %d", topicId));

        } catch (Exception e) {
            log.error("Error queuing topic notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при постановке уведомления в очередь");
        }
    }
}
