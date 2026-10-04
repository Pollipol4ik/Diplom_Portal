package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Оценка по критерию за работу")
public record CriterionGradeResponseDto(

        Long id,

        @JsonProperty("criterion_id")
        Long criterionId,

        @JsonProperty("criterion_name")
        String criterionName,

        @JsonProperty("max_points")
        Integer maxPoints,

        Integer points
) {}
