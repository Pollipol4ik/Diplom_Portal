package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Создание критерия оценивания")
public record CreateGradingCriterionRequestDto(

        @NotBlank
        @Size(max = 500)
        String name,

        String description,

        @NotNull
        @Min(0)
        @JsonProperty("max_points")
        Integer maxPoints
) {}
