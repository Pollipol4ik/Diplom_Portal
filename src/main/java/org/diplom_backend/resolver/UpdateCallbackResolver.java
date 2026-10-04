package org.diplom_backend.resolver;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.commands.CommandChain;

@RequiredArgsConstructor
@Slf4j
public class UpdateCallbackResolver extends UpdateResolver {

    private final CommandChain commandChain;

    @Override
    public SendMessage resolve(Update update) {
        if (update.callbackQuery() == null) {
            return resolveNext(update);
        }

        long chatId = update.callbackQuery().from().id();
        String callbackData = update.callbackQuery().data();

        log.info("Processing callback from chatId {}: {}", chatId, callbackData);

        try {
            return commandChain.executeCommand(callbackData, chatId);
        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage(), e);
            return new SendMessage(chatId, "⚠️ Произошла ошибка при обработке запроса");
        }
    }
}