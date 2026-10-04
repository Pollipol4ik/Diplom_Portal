package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Назначение модератора на школу")
public record SchoolModeratorResponseDto(

        @Schema(description = "ID записи назначения", example = "1")
        Long id,

        @Schema(description = "ID аккаунта модератора", example = "5")
        @JsonProperty("account_id") Long accountId,

        @Schema(description = "Никнейм модератора", example = "moderator_anna")
        String nickname,

        @Schema(description = "ID школы", example = "1")
        @JsonProperty("school_id") Long schoolId,

        @Schema(description = "Название школы", example = "Гимназия №1")
        @JsonProperty("school_name") String schoolName,

        @Schema(description = "Дата назначения")
        @JsonProperty("assigned_at") LocalDateTime assignedAt

) {}