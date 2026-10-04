package org.diplom_backend.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о подписке
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {

    private Long id;

    private Long accountId;

    private String type;

    private Integer courseNumber;

    private String courseName;

    private Long subjectId;

    private String subjectName;

    private Long topicId;

    private String topicName;

    private LocalDateTime createdAt;

    private Boolean isActive;
}
