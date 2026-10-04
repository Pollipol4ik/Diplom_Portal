package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.diplom_backend.model.SubmissionStatus;

@Schema(description = "Запрос на проверку работы ученика")
public record ReviewSubmissionRequestDto(

        @Schema(description = "Новый статус работы", example = "ACCEPTED")
        @NotNull(message = "Статус обязателен")
        SubmissionStatus status,

        @Schema(description = "Комментарий проверяющего")
        @Size(max = 5000, message = "Комментарий не может быть длиннее 5000 символов")
        @JsonProperty("reviewer_comment")
        String reviewerComment,

        @Schema(description = "Баллы за работу (0-100)")
        @Min(value = 0, message = "Балл не может быть меньше 0")
        @Max(value = 100, message = "Балл не может быть больше 100")
        Integer score
) {}
