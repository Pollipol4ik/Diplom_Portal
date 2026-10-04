package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.diplom_backend.model.ProjectStatus;

@Schema(description = "Рецензия модератора на слушание")
public record ReviewHearingRequestDto(

        @Schema(description = "Комментарий")
        String comment,

        @Schema(description = "Оценка (1-10)")
        @Min(1) @Max(10)
        Integer grade,

        @Schema(description = "Новый статус работы")
        @NotNull(message = "Статус обязателен")
        @JsonProperty("new_status")
        ProjectStatus newStatus
) {}
