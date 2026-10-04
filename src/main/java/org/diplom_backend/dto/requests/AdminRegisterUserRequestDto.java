package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRegisterUserRequestDto(
        @Email @NotBlank String email,
        @NotBlank @Size(max = 150) String nickname,
        @NotBlank @Size(min = 8, max = 200) String password,
        @JsonProperty("role_id") Long roleId,
        @JsonProperty("school_id") Long schoolId,
        @JsonProperty("class_id") Long classId
) {}
