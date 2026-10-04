package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Назначение модератора на курс")
public record CourseModeratorResponseDto(

        @Schema(description = "ID записи")
        Long id,

        @Schema(description = "ID аккаунта модератора")
        @JsonProperty("account_id") Long accountId,

        @Schema(description = "Никнейм модератора")
        String nickname,

        @Schema(description = "ID курса")
        @JsonProperty("course_id") Long courseId,

        @Schema(description = "Название курса")
        @JsonProperty("course_name") String courseName,

        @Schema(description = "Описание курса")
        @JsonProperty("course_description") String courseDescription,

        @Schema(description = "Школы курса")
        @JsonProperty("course_schools") List<SchoolResponseDto> courseSchools,

        @Schema(description = "Курс активен")
        @JsonProperty("is_active") Boolean isActive,

        @Schema(description = "Вводный курс")
        @JsonProperty("is_introduction") Boolean isIntroduction,

        @Schema(description = "Курс для отстающих")
        @JsonProperty("for_lagging_students") Boolean forLaggingStudents,

        @Schema(description = "Количество уроков")
        @JsonProperty("lesson_count") Integer lessonCount,

        @Schema(description = "Дата назначения")
        @JsonProperty("assigned_at") LocalDateTime assignedAt
) {}