package org.diplom_backend.dto.requests;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.diplom_backend.validation.constraints.ValidEmail;

public record AccountLoginRequestDto(@Size(max = 150, message = "Слишком длинный email")
                                     @NotBlank(message = "Email не должен быть пустым")
                                     @Schema(description = "Email пользователя", example = "admin@admin.ru")
                                     @ValidEmail
                                     String email,
                                     @Size(max = 50, message = "Слишком длинный пароль")
                                     @NotBlank(message = "Пароль не должен быть пустым")
                                     @Schema(description = "Пароль пользователя", example = "admin")
                                     String password) {
}
