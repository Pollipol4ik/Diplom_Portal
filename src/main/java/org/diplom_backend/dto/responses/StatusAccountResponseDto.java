package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatusAccountResponseDto(Long id, @JsonProperty("is_banned") Boolean isBanned) {
}
