package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record GetAllUserResponseDto(
        Long id,
        String email,
        String nickname,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("middle_name") String middleName,
        @JsonProperty("photo_name_in_directory") String photoNameInDirectory,
        @JsonProperty("school_id") Long schoolId,
        @JsonProperty("school_name") String schoolName,
        @JsonProperty("class_id") Long classId,
        @JsonProperty("class_name") String className,
        String role,
        @JsonProperty("is_banned") Boolean isBanned,
        @JsonProperty("is_lagging") Boolean isLagging,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
}