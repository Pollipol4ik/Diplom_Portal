package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record AccountResponseDto(
        Long id,
        String nickname,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("middleName") String middleName,
        String email,
        @JsonProperty("birthDate") String birthDate,
        String description,
        @JsonProperty("isBanned") Boolean isBanned,
        String role,
        @JsonProperty("photoNameInDirectory") String photoNameInDirectory,
        @JsonProperty("schoolId") Long schoolId,
        @Schema(description = "Название школы", example = "Гимназия №1")
        @JsonProperty("schoolName") String schoolName,
        @JsonProperty("classId") Long classId,
        @Schema(description = "Название класса", example = "10-А")
        @JsonProperty("className") String className
) {
}