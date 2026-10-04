package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Запрос на создание группы в курсе")
public record CreateCourseGroupRequestDto(

        @Schema(description = "Название темы проекта")
        @NotBlank(message = "Название группы (тема) обязательно")
        @Size(max = 200)
        String title,

        @Schema(description = "Описание проекта")
        String description,

        @Schema(description = "ID школы")
        @NotNull(message = "ID школы обязателен")
        @JsonProperty("school_id")
        Long schoolId,

        @Schema(description = "ID учеников (первый будет владельцем)")
        @JsonProperty("student_ids")
        List<Long> studentIds
) {}
