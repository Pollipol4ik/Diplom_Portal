package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.diplom_backend.model.SubmissionStatus;

import java.util.List;

@Schema(description = "Оценивание работы по критериям")
public record GradeSubmissionRequestDto(

        @JsonProperty("reviewer_comment")
        String reviewerComment,

        @NotNull
        List<CriterionScore> grades,

        /** Статус после проверки. Если не передан — по умолчанию ACCEPTED. */
        @JsonProperty("new_status")
        SubmissionStatus newStatus
) {
    public record CriterionScore(
            @NotNull
            @JsonProperty("criterion_id")
            Long criterionId,

            @NotNull
            @Min(0)
            Integer points
    ) {}
}