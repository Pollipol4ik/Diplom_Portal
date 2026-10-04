package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @JsonProperty("current_password") @NotBlank String currentPassword,
        @JsonProperty("new_password") @NotBlank @Size(min = 8, max = 200) String newPassword
) {}
