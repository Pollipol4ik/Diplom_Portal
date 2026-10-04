package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ДТО для получения направления (например, Инженерная школа)
 */
public record DirectionResponseDto(
        @Schema(description = "ID направления", example = "1")
        Long id,

        @Schema(description = "Наименование направления", example = "Инженерная школа")
        String name
) {
}