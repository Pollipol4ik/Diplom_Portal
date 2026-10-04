package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Критерий оценивания")
public record GradingCriterionResponseDto(

        Long id,

        @JsonProperty("order_number")
        Integer orderNumber,

        String name,

        String description,

        @JsonProperty("max_points")
        Integer maxPoints
) {}
