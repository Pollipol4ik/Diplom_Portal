package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание курса проектной деятельности")
public record CreateCourseRequestDto(

        @Schema(description = "Название курса", example = "Инновации умного города. Умная школа")
        @NotBlank(message = "Название курса не может быть пустым")
        @Size(max = 300, message = "Название курса не может быть длиннее 300 символов")
        String name,

        @Schema(description = "Описание курса")
        String description,

        @Schema(description = "ID школы (опционально, можно добавить позже)")
        @JsonProperty("school_id")
        Long schoolId,

        @Schema(description = "Вводный курс — только уроки-занятия, этапы слушаний не создаются автоматически")
        @JsonProperty("is_introduction")
        Boolean isIntroduction,

        @Schema(description = "Курс для отстающих — создаются этапы слушаний, доступен ученикам с флагом is_lagging")
        @JsonProperty("for_lagging_students")
        Boolean forLaggingStudents

) {}