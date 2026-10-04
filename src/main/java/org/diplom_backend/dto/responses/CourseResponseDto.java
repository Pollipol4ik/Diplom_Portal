package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Курс проектной деятельности (полный)")
public record CourseResponseDto(

        @Schema(description = "ID курса")
        Long id,

        @Schema(description = "Название курса")
        String name,

        @Schema(description = "Описание курса")
        String description,

        @Schema(description = "Привязанные школы")
        List<SchoolResponseDto> schools,

        @Schema(description = "Курс активен")
        @JsonProperty("is_active")
        Boolean isActive,

        @Schema(description = "Курс для отстающих")
        @JsonProperty("for_lagging_students")
        Boolean forLaggingStudents,

        @Schema(description = "Вводный курс: виден всем ученикам школы")
        @JsonProperty("is_introduction")
        Boolean isIntroduction,

        @Schema(description = "Дата создания")
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Уроки курса")
        List<CourseLessonResponseDto> lessons
) {}