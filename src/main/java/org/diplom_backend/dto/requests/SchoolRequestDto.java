package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SchoolRequestDto(
        @NotBlank(message = "Название школы не должно быть пустым")
        @Size(max = 255, message = "Название школы слишком длинное")
        @Schema(description = "Название учебного заведения", example = "Гимназия №1")
        String name
) {
}