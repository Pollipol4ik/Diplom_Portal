package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SchoolClassRequestDto(
        @NotBlank(message = "Название класса не должно быть пустым")
        @Size(max = 50, message = "Название класса слишком длинное")
        @Schema(description = "Наименование класса", example = "10-А")
        String name,

        @NotNull(message = "ID школы обязателен")
        @Schema(description = "ID школы, к которой принадлежит класс", example = "1")
        Long schoolId
) {
}