package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Передать идею из банка группе курса")
public record AssignIdeaToGroupRequestDto(
        @NotNull
        @JsonProperty("group_id")
        Long groupId
) {
}

