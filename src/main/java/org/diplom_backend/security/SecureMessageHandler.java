package org.diplom_backend.security;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.commands.AuthCommand;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecureMessageHandler {

    private final AuthCommand authCommand;
    private final TelegramStateService telegramStateService;

    /**
     * Обрабатывает сообщения с паролями для автоматического удаления
     * Возвращает true, если сообщение было обработано как пароль
     */
    public boolean handlePasswordMessage(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }

        long chatId = update.message().chat().id();
        int messageId = update.message().messageId();
        String text = update.message().text().trim();

        if (text.startsWith("/")) {
            return false;
        }

        var state = telegramStateService.getAuthState(chatId);
        if (state == TelegramStateService.UserAuthState.WAITING_PASSWORD) {
            log.info("Detected password message for deletion: chatId={}, messageId={}",
                    chatId, messageId);
            authCommand.registerPasswordMessageForDeletion(chatId, messageId);
            return true;
        }

        return false;
    }

    public void cleanupPasswordMessages(long chatId) {
        authCommand.cleanupPasswordMessages(chatId);
    }
}