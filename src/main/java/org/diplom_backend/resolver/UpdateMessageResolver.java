package org.diplom_backend.resolver;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.commands.CommandChain;
import org.diplom_backend.security.SecureMessageHandler;

@RequiredArgsConstructor
@Slf4j
public class UpdateMessageResolver extends UpdateResolver {

    private final CommandChain commandChain;
    private final SecureMessageHandler secureMessageHandler;

    @Override
    public SendMessage resolve(Update update) {

        if (update.message() == null || update.message().text() == null) {
            return resolveNext(update);
        }

        String messageText = update.message().text();
        long chatId = update.message().chat().id();

        log.info("Processing message from chatId {}: {}", chatId, messageText);

        secureMessageHandler.handlePasswordMessage(update);

        return commandChain.executeCommand(messageText, chatId);
    }
}