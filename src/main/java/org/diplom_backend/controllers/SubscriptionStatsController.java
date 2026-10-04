package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.dto.requests.SubscriptionStatsDTO;
import org.diplom_backend.model.UserSubscriptionEntity.SubscriptionType;
import org.diplom_backend.repositories.TelegramAccountRepository;
import org.diplom_backend.repositories.UserSubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для получения статистики по подпискам (только для админов)
 */
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "API для получения статистики")
@SecurityRequirement(name = "Bearer Authentication")
public class SubscriptionStatsController {

    private final UserSubscriptionRepository subscriptionRepository;
    private final TelegramAccountRepository telegramAccountRepository;

    /**
     * Получить общую статистику по подпискам
     */
    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить статистику подписок",
            description = "Возвращает общую статистику по всем подпискам (только для админов)")
    public ResponseEntity<SubscriptionStatsDTO> getSubscriptionStats() {
        try {
            long totalSubscriptions = subscriptionRepository.count();

            long newsSubscriptions = subscriptionRepository
                    .findAllNewsSubscribers().size();

            long courseSubscriptions = subscriptionRepository
                    .findAll().stream()
                    .filter(s -> s.getType() == SubscriptionType.DIRECTION && s.getIsActive())
                    .count();

            long subjectSubscriptions = subscriptionRepository
                    .findAll().stream()
                    .filter(s -> s.getType() == SubscriptionType.SUBJECT && s.getIsActive())
                    .count();

            long topicSubscriptions = subscriptionRepository
                    .findAll().stream()
                    .filter(s -> s.getType() == SubscriptionType.TOPIC && s.getIsActive())
                    .count();

            long activeUsers = telegramAccountRepository.count();

            SubscriptionStatsDTO stats = SubscriptionStatsDTO.builder()
                    .totalSubscriptions(totalSubscriptions)
                    .newsSubscriptions(newsSubscriptions)
                    .courseSubscriptions(courseSubscriptions)
                    .subjectSubscriptions(subjectSubscriptions)
                    .topicSubscriptions(topicSubscriptions)
                    .activeUsers(activeUsers)
                    .build();

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Error fetching subscription stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
