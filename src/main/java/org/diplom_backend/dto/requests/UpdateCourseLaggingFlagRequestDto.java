package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Признак курса для автоматического подключения отстающих учеников по школам курса")
public record UpdateCourseLaggingFlagRequestDto(

        @NotNull
        @JsonProperty("for_lagging_students")
        Boolean forLaggingStudents
) {}
