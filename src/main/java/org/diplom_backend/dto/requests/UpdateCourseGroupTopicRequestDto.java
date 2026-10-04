package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Изменение темы и описания проектной группы учеником")
public record UpdateCourseGroupTopicRequestDto(

        @Schema(description = "Название темы")
        String title,

        @Schema(description = "Описание темы")
        String description
) {}
