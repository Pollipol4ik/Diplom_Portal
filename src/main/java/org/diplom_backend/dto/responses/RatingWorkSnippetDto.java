package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Фрагмент работы ученика для показа из рейтинга (курс или слушание). */
public record RatingWorkSnippetDto(
        @JsonProperty("kind") String kind,
        @JsonProperty("course_id") Long courseId,
        @JsonProperty("course_name") String courseName,
        @JsonProperty("lesson_id") Long lessonId,
        @JsonProperty("lesson_title") String lessonTitle,
        @JsonProperty("group_title") String groupTitle,
        @JsonProperty("text_content") String textContent,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("file_name_in_directory") String fileNameInDirectory,
        @JsonProperty("score") Integer score,
        @JsonProperty("representative_grade") Integer representativeGrade,
        @JsonProperty("status") String status
) {}
