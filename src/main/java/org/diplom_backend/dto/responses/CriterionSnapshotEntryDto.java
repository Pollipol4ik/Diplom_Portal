package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CriterionSnapshotEntryDto(
        @JsonProperty("criterion_id") Long criterionId,
        @JsonProperty("criterion_name") String criterionName,
        @JsonProperty("max_points") int maxPoints,
        int points
) {}
