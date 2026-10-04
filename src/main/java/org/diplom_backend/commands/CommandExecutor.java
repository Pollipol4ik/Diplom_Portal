package org.diplom_backend.commands;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;

public interface CommandExecutor {

    SendMessage execute(String command, long chatId);

    String getCommandName();

    @Autowired
    default void registerMyself(CommandChain commandChain) {
        commandChain.registerCommand(getCommandName(), this);
    }
}
