package org.diplom_backend.resolver;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public abstract class UpdateResolver {

    private UpdateResolver next;

    public static UpdateResolver link(UpdateResolver first, UpdateResolver... chain) {
        UpdateResolver head = first;
        for (UpdateResolver resolver : chain) {
            head.next = resolver;
            head = resolver;
        }
        return first;
    }

    public abstract SendMessage resolve(Update update);

    protected SendMessage resolveNext(Update update) {
        if (next == null) {
            throw new RuntimeException("Unhandled update type");
        }
        return next.resolve(update);
    }
}