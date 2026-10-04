package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Запрос ученика на выбор темы (создание своей группы в курсе)")
public record CreateMyCourseGroupRequestDto(

        @Schema(description = "Название темы проекта")
        @NotBlank(message = "Название темы обязательно")
        @Size(max = 200)
        String title,

        @Schema(description = "Описание темы")
        String description,

        @Schema(description = "ID одноклассников из того же класса (опционально)")
        @JsonProperty("classmate_account_ids")
        List<Long> classmateAccountIds
) {
}

