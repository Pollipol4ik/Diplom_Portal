package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Ответ ученика на комментарий/оценку по работе")
public record CreateSubmissionReviewReplyRequestDto(

        @Schema(description = "Текст ответа", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Текст ответа не может быть пустым")
        @JsonProperty("comment")
        String comment
) {}
