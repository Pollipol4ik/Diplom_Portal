package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.diplom_backend.model.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Отправка работы на слушание")
public record HearingSubmissionResponseDto(

        @Schema(description = "ID отправки")
        Long id,

        @Schema(description = "ID урока-слушания")
        @JsonProperty("lesson_id")
        Long lessonId,

        @Schema(description = "ID группы")
        @JsonProperty("group_id")
        Long groupId,

        @Schema(description = "Имя файла")
        @JsonProperty("file_name")
        String fileName,

        @Schema(description = "Имя файла в хранилище")
        @JsonProperty("file_name_in_directory")
        String fileNameInDirectory,

        @Schema(description = "Статус")
        ProjectStatus status,

        @Schema(description = "Версия")
        @JsonProperty("current_version")
        Integer currentVersion,

        @Schema(description = "Дата отправки")
        @JsonProperty("submitted_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime submittedAt,

        @Schema(description = "Дата обновления")
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt,

        @Schema(description = "Рецензии")
        List<HearingReviewResponseDto> reviews
) {}
