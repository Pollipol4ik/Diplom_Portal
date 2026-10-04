package org.diplom_backend;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.message_sender.Sender;
import org.diplom_backend.resolver.UpdateResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BotUpdatesListener implements UpdatesListener {
    @Qualifier("sender")
    private final Sender messageSenderImpl;

    private final UpdateResolver updateResolver;

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            processUpdate(update);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUpdate(Update update) {
        SendMessage message = updateResolver.resolve(update);
        messageSenderImpl.sendMessage(message);
    }
}
