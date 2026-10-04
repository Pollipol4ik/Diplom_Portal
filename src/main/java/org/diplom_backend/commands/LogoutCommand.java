package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.stereotype.Component;

import static org.diplom_backend.commands.Command.LOGOUT;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogoutCommand implements CommandExecutor {

    private final TelegramStateService stateService;

    @Override
    public SendMessage execute(String message, long chatId) {
        log.info("Logout command received for chatId: {}", chatId);

        if (!stateService.isUserAuthenticated(chatId)) {
            return new SendMessage(chatId, "⚠️ Вы не авторизованы.")
                    .parseMode(ParseMode.HTML);
        }

        String nickname = stateService.getUserNickname(chatId);
        stateService.logoutUser(chatId); // Удаляет сессию из кэша и запись из БД

        return new SendMessage(chatId,
                String.format("✅ Вы успешно вышли из аккаунта <b>%s</b>.\n\n" +
                        "Чтобы снова пользоваться функциями бота, используйте /auth.", nickname))
                .parseMode(ParseMode.HTML);
    }

    @Override
    public String getCommandName() {
        return LOGOUT.getName();
    }
}