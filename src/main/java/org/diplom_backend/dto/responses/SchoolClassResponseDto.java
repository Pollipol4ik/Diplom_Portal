package org.diplom_backend.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolClassResponseDto(
        @Schema(description = "ID класса", example = "1")
        Long id,

        @Schema(description = "Наименование класса", example = "10-А")
        String name,

        @Schema(description = "ID школы", example = "1")
        Long schoolId
) {
}