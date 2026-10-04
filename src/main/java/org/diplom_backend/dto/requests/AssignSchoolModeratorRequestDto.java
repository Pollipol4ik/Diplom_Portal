package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AssignSchoolModeratorRequestDto(

        @Schema(description = "ID аккаунта модератора", example = "5")
        @NotNull
        @JsonProperty("account_id")
        Long accountId,

        @Schema(description = "ID школы", example = "1")
        @NotNull
        @JsonProperty("school_id")
        Long schoolId

) {}