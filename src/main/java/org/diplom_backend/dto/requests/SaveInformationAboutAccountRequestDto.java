package org.diplom_backend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SaveInformationAboutAccountRequestDto(
        @Schema(description = "Nickname пользователя", example = "admin")
        @Size(max = 150, message = "Слишком длинный nickname")
        @NotBlank
        String nickname,

        @Schema(description = "Описание пользователя", example = "Я люблю капибар")
        @Size(max = 250, message = "Слишком длинное описание")
        String description,

        @Schema(description = "Имя пользователя", example = "Вася")
        @Size(max = 150, message = "Слишком длинное имя")
        @JsonProperty("firstName")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Пупкин")
        @Size(max = 150, message = "Слишком длинная фамилия")
        @JsonProperty("lastName")
        String lastName,

        @Schema(description = "Отчество пользователя", example = "Васильевич")
        @Size(max = 150, message = "Слишком длинное отчество")
        @JsonProperty("middleName")
        String middleName,

        @Schema(description = "Дата рождения пользователя", example = "2000-01-01")
        @Size(max = 50, message = "Слишком длинная дата рождения")
        @JsonProperty("birthDate")
        String birthDate,

        @Schema(description = "ID школы", example = "1")
        @Min(value = 1, message = "ID школы должен быть положительным")
        @JsonProperty("schoolId")
        Long schoolId,

        @Schema(description = "ID класса", example = "5")
        @Min(value = 1, message = "ID класса должен быть положительным")
        @JsonProperty("classId")
        Long classId,

        @Schema(description = "Фотография профиля")
        MultipartFile photo
) {
}