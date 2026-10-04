package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ДТО-ответ для топиков предметов
 */
@Schema(description = "Ответ с информацией о топике предмета")
public record SubjectTopicResponseDto(
        @Schema(description = "ID топика")
        Long id,

        @Schema(description = "Название топика (например, 'Экзамен', 'Лабораторная')")
        String name,

        @Schema(description = "Информация о предмете")
        @JsonProperty("subject")
        SubjectResponseDto subjectResponseDTO
) {}