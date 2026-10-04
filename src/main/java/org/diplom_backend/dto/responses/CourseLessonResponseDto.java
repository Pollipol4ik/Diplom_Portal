package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.diplom_backend.model.HearingStage;
import org.diplom_backend.model.LessonCategory;
import org.diplom_backend.model.SubmissionType;

import java.time.LocalDateTime;

@Schema(description = "Урок (тема) курса")
public record CourseLessonResponseDto(

        @Schema(description = "ID урока")
        Long id,

        @Schema(description = "Порядковый номер")
        @JsonProperty("order_number")
        Integer orderNumber,

        @Schema(description = "Название темы")
        String title,

        @Schema(description = "Категория: LESSON или HEARING")
        LessonCategory category,

        @Schema(description = "Этап слушания (только для HEARING)")
        @JsonProperty("hearing_stage")
        HearingStage hearingStage,

        @Schema(description = "Содержание лекции")
        @JsonProperty("lecture_content")
        String lectureContent,

        @Schema(description = "Описание практического задания")
        @JsonProperty("practice_description")
        String practiceDescription,

        @Schema(description = "Тип сдачи задания")
        @JsonProperty("submission_type")
        SubmissionType submissionType,

        @Schema(description = "Имя файла лекции")
        @JsonProperty("lecture_file_name")
        String lectureFileName,

        @Schema(description = "Имя файла лекции в хранилище")
        @JsonProperty("lecture_file_name_in_directory")
        String lectureFileNameInDirectory,

        @Schema(description = "Ссылка на видео")
        @JsonProperty("video_url")
        String videoUrl,

        @Schema(description = "Максимальный балл")
        @JsonProperty("max_score")
        Integer maxScore,

        @Schema(description = "Крайний срок сдачи работы")
        @JsonProperty("submission_deadline")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime submissionDeadline,

        @Schema(description = "Этап слушания открыт учениками (для этапов 2+)")
        @JsonProperty("hearing_open_for_students")
        Boolean hearingOpenForStudents
) {}
