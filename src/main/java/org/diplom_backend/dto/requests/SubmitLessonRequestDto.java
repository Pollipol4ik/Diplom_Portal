package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на сдачу практического задания")
public record SubmitLessonRequestDto(

        @Schema(description = "Текстовый ответ")
        @Size(max = 10000, message = "Текст ответа не может быть длиннее 10 000 символов")
        @JsonProperty("text_content")
        String textContent
) {}
