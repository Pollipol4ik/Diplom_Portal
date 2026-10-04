package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDirectionRequestDto (
        @Schema(description = "Заголовок публикации", example = "Контрольная работа 1")
        @Size(max = 150, message = "Слишком длинный заголовок публикации")
        @NotBlank(message = "Заголовок публикации не может быть пустым")
        String name
) {
}
