package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record SchoolResponseDto(
        @Schema(description = "ID школы", example = "1")
        Long id,

        @Schema(description = "Название школы", example = "Гимназия №1")
        String name,

        @Schema(description = "Дедлайн выбора темы проекта")
        @JsonProperty("topic_deadline")
        LocalDate topicDeadline
) {
}