package org.diplom_backend.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для запроса на отправку уведомления
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    @NotBlank(message = "Заголовок уведомления обязателен")
    @Size(max = 100, message = "Заголовок не должен превышать 100 символов")
    private String title;

    @NotBlank(message = "Текст уведомления обязателен")
    @Size(max = 4000, message = "Текст уведомления не должен превышать 4000 символов")
    private String message;
}
