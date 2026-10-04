package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseSummaryResponseDto {

    @JsonProperty("lessons")
    private List<LessonHeader> lessons;

    @JsonProperty("students")
    private List<StudentRow> students;

    @Data
    @Builder
    public static class LessonHeader {
        private long id;
        private String title;
        @JsonProperty("max_score")
        private int maxScore;
        private String category;
    }

    @Data
    @Builder
    public static class StudentRow {
        @JsonProperty("account_id")
        private long accountId;
        private String nickname;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("school_name")
        private String schoolName;
        @JsonProperty("class_name")
        private String className;
        @JsonProperty("is_lagging")
        private Boolean isLagging;
        @JsonProperty("scores")
        private List<LessonScore> scores;
        @JsonProperty("total_score")
        private int totalScore;
    }

    @Data
    @Builder
    public static class LessonScore {
        @JsonProperty("lesson_id")
        private long lessonId;
        private String status;
        private Integer score;
    }
}
