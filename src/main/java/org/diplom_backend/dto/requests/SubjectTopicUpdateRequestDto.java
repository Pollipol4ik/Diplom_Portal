package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для обновления названия топика (без subjectId)
 */
public record SubjectTopicUpdateRequestDto(
        @NotBlank(message = "Название топика обязательно")
        @Schema(description = "Название топика", example = "Введение в программирование")
        String name
) {}