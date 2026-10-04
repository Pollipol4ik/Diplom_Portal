package org.diplom_backend.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import org.diplom_backend.commands.CommandChain;
import org.diplom_backend.resolver.UpdateCallbackResolver;
import org.diplom_backend.resolver.UpdateMessageResolver;
import org.diplom_backend.resolver.UpdateResolver;
import org.diplom_backend.security.SecureMessageHandler;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.diplom_backend.commands.Command.AUTH;
import static org.diplom_backend.commands.Command.HELP;
import static org.diplom_backend.commands.Command.LOGOUT;
import static org.diplom_backend.commands.Command.START;
import static org.diplom_backend.commands.Command.SUB;
import static org.diplom_backend.commands.Command.UNSUB;


@Configuration
public class BotConfiguration {

    @Bean
    public UpdateResolver updateResolver(CommandChain commandChain, SecureMessageHandler handler) {
        return UpdateResolver.link(
                new UpdateMessageResolver(commandChain, handler),
                new UpdateCallbackResolver(commandChain)
        );
    }

    @Bean
    public BotCommand[] commands() {
        return new BotCommand[]{
                new BotCommand(START.getName(), START.getDescription()),
                new BotCommand(AUTH.getName(), AUTH.getDescription()),
                new BotCommand(HELP.getName(), HELP.getDescription()),
                new BotCommand(SUB.getName(), SUB.getDescription()),
                new BotCommand(UNSUB.getName(), UNSUB.getDescription()),
                new BotCommand(LOGOUT.getName(), LOGOUT.getDescription())
        };
    }

    @Bean
    public TelegramBot bot(AppConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }
}
