package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Курс проектной деятельности (краткий)")
public record CourseShortResponseDto(

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

        @Schema(description = "Курс для отстающих: автоматическое создание группы при дедлайне и при привязке школы")
        @JsonProperty("for_lagging_students")
        Boolean forLaggingStudents,

        @Schema(description = "Вводный курс: виден всем ученикам школы независимо от статуса отстающего")
        @JsonProperty("is_introduction")
        Boolean isIntroduction,

        @Schema(description = "Количество уроков")
        @JsonProperty("lesson_count")
        Integer lessonCount,

        @Schema(description = "Дата создания")
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {}