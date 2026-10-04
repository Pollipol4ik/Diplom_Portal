package org.diplom_backend.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для статистики подписок
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatsDTO {

    private Long totalSubscriptions;

    private Long newsSubscriptions;

    private Long courseSubscriptions;

    private Long subjectSubscriptions;

    private Long topicSubscriptions;

    private Long activeUsers;
}
