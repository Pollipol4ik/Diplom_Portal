package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для создания топика предмета
 */
public record SubjectTopicRequestDto(
        @NotBlank(message = "Название топика обязательно")
        @Schema(description = "Название топика", example = "Введение в программирование")
        String name,

        @NotNull(message = "ID предмета обязателен")
        @Schema(description = "ID предмета", example = "1")
        Long subjectId
) {}