package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddTeammateRequestDto(

        @Schema(description = "ID аккаунта сокомандника", example = "42")
        @NotNull(message = "ID участника не может быть пустым")
        @JsonProperty("account_id")
        Long accountId

) {}
