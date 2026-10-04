package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StudentRatingResponseDto(
        @JsonProperty("account_id") Long accountId,
        String nickname,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("middle_name") String middleName,
        @JsonProperty("school_name") String schoolName,
        @JsonProperty("class_name") String className,
        @JsonProperty("project_rating") Double projectRating,
        @JsonProperty("course_rating") Double courseRating,
        @JsonProperty("combined_rating") Double combinedRating,
        @JsonProperty("is_lagging") Boolean isLagging,
        @JsonProperty("course_rating_work") RatingWorkSnippetDto courseRatingWork,
        @JsonProperty("project_rating_work") RatingWorkSnippetDto projectRatingWork,
        @JsonProperty("enrolled_course_ids") List<Long> enrolledCourseIds,
        @JsonProperty("forced_course_ids") List<Long> forcedCourseIds
) {
}