package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на перенос отстающего ученика в курс для отстающих")
public record TransferLaggingRequestDto(

        @Schema(description = "ID целевого курса, куда перевести ученика")
        @NotNull(message = "ID целевого курса обязателен")
        @JsonProperty("target_course_id")
        Long targetCourseId
) {
}