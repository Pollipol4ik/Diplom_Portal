package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Запрос на обновление группы в курсе")
public record UpdateCourseGroupRequestDto(

        @Schema(description = "Новое название темы проекта")
        String title,

        @Schema(description = "Новое описание проекта")
        String description,

        @Schema(description = "Новый список ID учеников (первый = владелец)")
        @JsonProperty("student_ids")
        List<Long> studentIds
) {}
