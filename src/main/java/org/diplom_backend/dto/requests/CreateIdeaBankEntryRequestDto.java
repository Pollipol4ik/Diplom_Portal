package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIdeaBankEntryRequestDto(

        @Schema(description = "Название темы")
        @NotBlank(message = "Название не может быть пустым")
        @Size(max = 200)
        String title,

        @Schema(description = "Описание идеи")
        @Size(max = 2000)
        String description,

        @Schema(description = "Комментарии / заметки")
        @Size(max = 5000)
        String comments,

        @Schema(description = "Баллы (оценка идеи)")
        @JsonProperty("score")
        Integer score,

        @Schema(description = "ID исходного проекта (если архивируется из проекта)")
        @JsonProperty("source_project_id")
        Long sourceProjectId
) {}
