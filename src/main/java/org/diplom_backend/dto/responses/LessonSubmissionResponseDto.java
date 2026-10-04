package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.diplom_backend.model.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Работа ученика по практическому заданию")
public record LessonSubmissionResponseDto(

        @Schema(description = "ID работы")
        Long id,

        @Schema(description = "ID урока")
        @JsonProperty("lesson_id")
        Long lessonId,

        @Schema(description = "Название урока")
        @JsonProperty("lesson_title")
        String lessonTitle,

        @Schema(description = "ID ученика")
        @JsonProperty("account_id")
        Long accountId,

        @Schema(description = "Никнейм ученика")
        @JsonProperty("account_nickname")
        String accountNickname,

        @Schema(description = "Текстовый ответ")
        @JsonProperty("text_content")
        String textContent,

        @Schema(description = "Имя загруженного файла")
        @JsonProperty("file_name")
        String fileName,

        @Schema(description = "Имя файла в хранилище")
        @JsonProperty("file_name_in_directory")
        String fileNameInDirectory,

        @Schema(description = "Статус работы")
        SubmissionStatus status,

        @Schema(description = "Комментарий проверяющего")
        @JsonProperty("reviewer_comment")
        String reviewerComment,

        @Schema(description = "Баллы за работу")
        Integer score,

        @Schema(description = "Максимальный балл за урок")
        @JsonProperty("max_score")
        Integer maxScore,

        @Schema(description = "Дата отправки")
        @JsonProperty("submitted_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime submittedAt,

        @Schema(description = "Дата обновления")
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,

        @Schema(description = "Оценки по критериям")
        @JsonProperty("criterion_grades")
        List<CriterionGradeResponseDto> criterionGrades
) {}
