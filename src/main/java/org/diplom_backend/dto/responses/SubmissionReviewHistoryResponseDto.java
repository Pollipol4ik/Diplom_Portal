package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.diplom_backend.model.SubmissionReviewHistoryKind;
import org.diplom_backend.model.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Запись в истории проверки работы")
public record SubmissionReviewHistoryResponseDto(

        @Schema(description = "ID записи")
        Long id,

        @Schema(description = "Тип записи в истории")
        @JsonProperty("entry_kind")
        SubmissionReviewHistoryKind entryKind,

        @Schema(description = "Статус после проверки")
        @JsonProperty("status_after")
        SubmissionStatus statusAfter,

        @Schema(description = "Итоговый балл (если есть)")
        Integer score,

        @Schema(description = "Комментарий проверяющего")
        @JsonProperty("reviewer_comment")
        String reviewerComment,

        @Schema(description = "Когда проверено")
        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Ник автора записи (проверяющего или ученика)")
        @JsonProperty("reviewer_nickname")
        String reviewerNickname,

        @Schema(description = "Снимок оценок по критериям (если проверка по критериям)")
        @JsonProperty("criterion_snapshot")
        List<CriterionSnapshotEntryDto> criterionSnapshot
) {}
