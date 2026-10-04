package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record IdeaBankEntryResponseDto(
        Long id,
        String title,
        String description,
        String comments,
        Integer score,
        @JsonProperty("created_by_nickname") String createdByNickname,
        @JsonProperty("source_project_id") Long sourceProjectId,
        @JsonProperty("course_id") Long courseId,
        @JsonProperty("course_name") String courseName,
        @JsonProperty("created_at") LocalDateTime createdAt
) {}