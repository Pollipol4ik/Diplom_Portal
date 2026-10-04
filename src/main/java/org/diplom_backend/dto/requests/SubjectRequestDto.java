package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ДТО для создания нового предмета
 * Автор: Полина Купцова
 */
public record SubjectRequestDto(
        @Schema(description = "Название предмета", example = "Математика")
        @NotBlank(message = "Название предмета не может быть пустым")
        @Size(max = 100, message = "Название предмета не может быть длиннее 100 символов")
        String name,

        @JsonProperty("direction_id")
        @NotNull(message = "ID направления не может быть пустым")
        @Schema(description = "ID направления, к которому относится предмет", example = "1")
        Long directionId
) {
}