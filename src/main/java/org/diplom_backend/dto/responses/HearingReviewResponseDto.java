package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Рецензия на слушание")
public record HearingReviewResponseDto(

        @Schema(description = "ID рецензии")
        Long id,

        @Schema(description = "ID модератора")
        @JsonProperty("moderator_id")
        Long moderatorId,

        @Schema(description = "Имя модератора")
        @JsonProperty("moderator_name")
        String moderatorName,

        @Schema(description = "Комментарий")
        String comment,

        @Schema(description = "Оценка")
        Integer grade,

        @Schema(description = "Дата проверки")
        @JsonProperty("reviewed_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime reviewedAt
) {}
