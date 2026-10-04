package org.diplom_backend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.commands.AuthCommand;
import org.diplom_backend.commands.CommandChain;
import org.diplom_backend.commands.HelpCommand;
import org.diplom_backend.commands.LogoutCommand;
import org.diplom_backend.commands.StartCommand;
import org.diplom_backend.commands.SubscribeCommand;
import org.diplom_backend.commands.UnsubscribeCommand;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CommandRegistrationConfig {

    private final CommandChain commandChain;
    private final StartCommand startCommand;
    private final HelpCommand helpCommand;
    private final AuthCommand authCommand;
    private final SubscribeCommand subscribeCommand;
    private final UnsubscribeCommand unsubscribeCommand;
    private final LogoutCommand logoutCommand;

    @PostConstruct
    public void registerCommands() {
        log.info("Registering bot commands...");

        commandChain.registerCommand("/start", startCommand);
        commandChain.registerCommand("/help", helpCommand);
        commandChain.registerCommand("/auth", authCommand);
        commandChain.registerCommand("/subscribe", subscribeCommand);
        commandChain.registerCommand("/unsubscribe", unsubscribeCommand);
        commandChain.registerCommand("/logout", logoutCommand);

        log.info("Bot commands registered successfully");
    }
}