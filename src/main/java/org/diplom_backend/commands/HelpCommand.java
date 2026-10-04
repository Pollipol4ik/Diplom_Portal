package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static org.diplom_backend.commands.Command.HELP;
import static org.diplom_backend.utils.MessagesUtils.HELP_MESSAGE;


@Log4j2
@Component
public class HelpCommand implements CommandExecutor {

    @Override
    public SendMessage execute(String command, long chatId) {
        log.info("Command /help has executed");
        return new SendMessage(chatId, HELP_MESSAGE).parseMode(ParseMode.HTML);
    }

    @Override
    public String getCommandName() {
        return HELP.getName();
    }
}
