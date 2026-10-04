package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Назначить идею ученику (создать группу по идее), только если ученик ещё не состоит в группе курса")
public record AssignIdeaToStudentRequestDto(
        @NotNull
        @JsonProperty("course_id")
        Long courseId,

        @NotNull
        @JsonProperty("student_id")
        Long studentId
) {
}

