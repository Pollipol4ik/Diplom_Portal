package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Информация о назначении модератора на предмет")
public record SubjectModeratorResponseDto(
        @Schema(description = "ID записи назначения", example = "1")
        Long id,

        @Schema(description = "ID аккаунта модератора", example = "5")
        @JsonProperty("accountId") Long accountId,

        @Schema(description = "Никнейм модератора", example = "ivan_mod")
        String nickname,

        @Schema(description = "ID предмета", example = "10")
        @JsonProperty("subjectId") Long subjectId,

        @Schema(description = "Название предмета", example = "Высшая математика")
        @JsonProperty("subjectName") String subjectName,

        @Schema(description = "Дата и время назначения")
        @JsonProperty("assignedAt") LocalDateTime assignedAt
) {
}