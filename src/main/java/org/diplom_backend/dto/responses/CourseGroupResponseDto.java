package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Группа (проектная команда) в курсе")
public record CourseGroupResponseDto(

        @Schema(description = "ID группы")
        Long id,

        @Schema(description = "ID курса")
        @JsonProperty("course_id")
        Long courseId,

        @Schema(description = "Название темы проекта")
        String title,

        @Schema(description = "Описание проекта")
        String description,

        @Schema(description = "ID школы")
        @JsonProperty("school_id")
        Long schoolId,

        @Schema(description = "Название школы")
        @JsonProperty("school_name")
        String schoolName,

        @Schema(description = "Участники группы")
        List<CourseGroupMemberResponseDto> members,

        @Schema(description = "Дата создания")
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {}
