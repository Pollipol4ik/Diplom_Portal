package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountCourseProgressResponseDto {

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("total_lessons")
    private int totalLessons;

    @JsonProperty("submitted_lessons")
    private int submittedLessons;

    @JsonProperty("accepted_lessons")
    private int acceptedLessons;

    @JsonProperty("average_score")
    private double averageScore;

    @JsonProperty("max_possible_score")
    private int maxPossibleScore;

    @JsonProperty("hearing_statuses")
    private List<HearingStageStatus> hearingStatuses;

    @Data
    @Builder
    public static class HearingStageStatus {
        private String stage;
        private String status;
    }
}
