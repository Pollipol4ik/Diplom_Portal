package org.diplom_backend;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.diplom_backend.security.SecureMessageHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Component
public class AcademicBot {

    private final TelegramBot bot;
    private final BotCommand[] commands;
    private final UpdatesListener updatesListener;
    private final SecureMessageHandler secureMessageHandler;


    @PostConstruct
    private void start() {
        log.info("Bot has been started");
        bot.execute(new SetMyCommands(commands));

        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                try {

                    if (update.message() != null) {
                        secureMessageHandler.handlePasswordMessage(update);
                    }
                    updatesListener.process(List.of(update));
                } catch (Exception e) {
                    log.error("Error processing update: {}", e.getMessage(), e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}