package org.diplom_backend.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для информации о Telegram аккаунте
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramAccountDTO {

    private Long id;

    private Long chatId;

    private Long accountId;

    private String accountNickname;

    private String accountEmail;

    private LocalDateTime createdAt;

    private LocalDateTime lastActive;
}
