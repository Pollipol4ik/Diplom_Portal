package org.diplom_backend.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания подписки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    @NotNull(message = "Тип подписки обязателен")
    private String type; // NEWS, COURSE, SUBJECT, TOPIC

    private Integer courseNumber;

    private Long subjectId;

    private Long topicId;
}
