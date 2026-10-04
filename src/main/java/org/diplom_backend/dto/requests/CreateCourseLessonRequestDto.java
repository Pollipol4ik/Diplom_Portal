package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.diplom_backend.model.HearingStage;
import org.diplom_backend.model.LessonCategory;
import org.diplom_backend.model.SubmissionType;

import java.time.LocalDateTime;

@Schema(description = "Запрос на создание урока (темы) в курсе")
public record CreateCourseLessonRequestDto(

        @Schema(description = "Название темы", example = "Основные этапы создания проекта")
        @NotBlank(message = "Название темы не может быть пустым")
        @Size(max = 500, message = "Название темы не может быть длиннее 500 символов")
        String title,

        @Schema(description = "Категория: LESSON (по умолчанию) или HEARING")
        LessonCategory category,

        @Schema(description = "Этап слушания (только для HEARING)")
        @JsonProperty("hearing_stage")
        HearingStage hearingStage,

        @Schema(description = "Содержание лекции (HTML или Markdown)")
        @JsonProperty("lecture_content")
        String lectureContent,

        @Schema(description = "Описание практического задания")
        @JsonProperty("practice_description")
        String practiceDescription,

        @Schema(description = "Тип сдачи практического задания", example = "TEXT")
        @NotNull(message = "Тип сдачи обязателен")
        @JsonProperty("submission_type")
        SubmissionType submissionType,

        @Schema(description = "Порядковый номер урока в курсе", example = "1")
        @NotNull(message = "Порядковый номер обязателен")
        @Min(value = 1, message = "Порядковый номер должен быть >= 1")
        @JsonProperty("order_number")
        Integer orderNumber,

        @Schema(description = "Ссылка на видео лекции", example = "https://youtube.com/watch?v=...")
        @JsonProperty("video_url")
        @Size(max = 1000)
        String videoUrl,

        @Schema(description = "Максимальный балл за урок (при критериях — сумма максимумов по критериям)", example = "100")
        @JsonProperty("max_score")
        @Min(value = 0, message = "Максимальный балл >= 0")
        @Max(value = 100000, message = "Максимальный балл слишком велик")
        Integer maxScore,

        @Schema(description = "Для слушаний (этапы 2+): открыт ли этап для учеников. Этап выбора темы всегда открыт.")
        @JsonProperty("hearing_open_for_students")
        Boolean hearingOpenForStudents,

        @Schema(description = "Крайний срок сдачи (не задан = без ограничения). Ожидается ISO-8601, напр. 2026-04-16T23:59:00")
        @JsonProperty("submission_deadline")
        LocalDateTime submissionDeadline
) {}
