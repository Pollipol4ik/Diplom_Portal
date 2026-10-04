package org.diplom_backend.message_sender;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class TelegramMessageSender implements Sender {

    private final TelegramBot bot;

    public SendResponse sendMessage(SendMessage message) {
        SendResponse response = bot.execute(message);
        log.info("Response is Ok: {}, Response description : {}", response.isOk(), response.description());
        return response;
    }
}
