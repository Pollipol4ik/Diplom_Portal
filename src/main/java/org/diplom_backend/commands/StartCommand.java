package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static org.diplom_backend.commands.Command.START;
import static org.diplom_backend.utils.MessagesUtils.WELCOME_MESSAGE;


@Log4j2
@Component
@RequiredArgsConstructor
public class StartCommand implements CommandExecutor {


    @Override
    public SendMessage execute(String command, long chatId) {
        log.info("Command /start has executed");
        return new SendMessage(chatId, WELCOME_MESSAGE).parseMode(ParseMode.HTML);
    }

    @Override
    public String getCommandName() {
        return START.getName();
    }
}
