package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.model.Account;
import org.diplom_backend.services.TelegramBotService;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.diplom_backend.commands.Command.AUTH;
import static org.diplom_backend.utils.MessagesUtils.AUTH_ALREADY_AUTHENTICATED;
import static org.diplom_backend.utils.MessagesUtils.AUTH_EMPTY_LOGIN;
import static org.diplom_backend.utils.MessagesUtils.AUTH_EMPTY_PASSWORD;
import static org.diplom_backend.utils.MessagesUtils.AUTH_ERROR;
import static org.diplom_backend.utils.MessagesUtils.AUTH_FAILED;
import static org.diplom_backend.utils.MessagesUtils.AUTH_REQUEST_LOGIN;
import static org.diplom_backend.utils.MessagesUtils.AUTH_SESSION_EXPIRED;
import static org.diplom_backend.utils.MessagesUtils.AUTH_SUCCESS;
import static org.diplom_backend.utils.MessagesUtils.AUTH_SUGGEST_SUBSCRIPTION;
import static org.diplom_backend.utils.MessagesUtils.AUTH_USER_NOT_FOUND;
import static org.diplom_backend.utils.MessagesUtils.AUTH_WRONG_PASSWORD;
import static org.diplom_backend.utils.MessagesUtils.ERROR_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Component
public class AuthCommand implements CommandExecutor {

    private final TelegramStateService stateService;
    private final CommandChain commandChain;
    private final TelegramBotService telegramBotService;

    private static final int PASSWORD_DELETE_DELAY_SECONDS = 10;
    private static final String PASSWORD_REQUEST_MESSAGE =
            "🔐 <b>Введите пароль:</b>\n" +
                    "⚠️ <i>Сообщение с паролем будет автоматически удалено через " +
                    PASSWORD_DELETE_DELAY_SECONDS + " секунд</i>";

    @Override
    public SendMessage execute(String message, long chatId) {
        TelegramStateService.UserAuthState currentState = stateService.getAuthState(chatId);

        // Если пользователь в процессе авторизации
        if (currentState == TelegramStateService.UserAuthState.WAITING_LOGIN
                || currentState == TelegramStateService.UserAuthState.WAITING_PASSWORD) {

            // Если это ввод пароля (не команда /auth)
            if (currentState == TelegramStateService.UserAuthState.WAITING_PASSWORD
                    && !message.trim().equals(AUTH.getName())) {

                // Помечаем, что нужно удалить это сообщение
                // ID сообщения будет передан через отдельный метод
                log.info("Password input detected for chatId: {}", chatId);
            }

            return handleAuthStep(message, chatId, currentState);
        }

        // Обычная логика при вводе команды /auth
        if (message.trim().equals(AUTH.getName())) {
            return startAuthProcess(chatId);
        }

        return new SendMessage(chatId, ERROR_MESSAGE)
                .parseMode(ParseMode.HTML);
    }

    private SendMessage handleAuthStep(String message, long chatId,
                                       TelegramStateService.UserAuthState state) {
        switch (state) {
            case WAITING_LOGIN:
                return processLoginInput(chatId, message);
            case WAITING_PASSWORD:
                return processPasswordInput(chatId, message);
            default:
                return new SendMessage(chatId, ERROR_MESSAGE).parseMode(ParseMode.HTML);
        }
    }

    @Override
    public String getCommandName() {
        return AUTH.getName();
    }

    private SendMessage startAuthProcess(long chatId) {
        if (stateService.isUserAuthenticated(chatId)) {
            String nickname = stateService.getUserNickname(chatId);
            return new SendMessage(chatId,
                    String.format(AUTH_ALREADY_AUTHENTICATED, nickname))
                    .parseMode(ParseMode.HTML);
        }

        stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_LOGIN);
        return new SendMessage(chatId, AUTH_REQUEST_LOGIN).parseMode(ParseMode.HTML);
    }

    private SendMessage processLoginInput(long chatId, String login) {
        login = login.trim();

        if (login.isEmpty()) {
            return new SendMessage(chatId, AUTH_EMPTY_LOGIN).parseMode(ParseMode.HTML);
        }

        try {
            // 1. Проверяем существование пользователя
            if (!stateService.userExists(login)) {
                stateService.clearAuthState(chatId);
                return new SendMessage(chatId,
                        String.format(AUTH_USER_NOT_FOUND, login))
                        .parseMode(ParseMode.HTML);
            }

            // 2. Получаем nickname пользователя
            String nickname = stateService.getUserNicknameByLogin(login);
            stateService.saveAuthData(chatId, login, nickname);
            stateService.setAuthState(chatId, TelegramStateService.UserAuthState.WAITING_PASSWORD);

            // Отправляем сообщение с предупреждением об автоматическом удалении
            return new SendMessage(chatId, PASSWORD_REQUEST_MESSAGE)
                    .parseMode(ParseMode.HTML);

        } catch (Exception e) {
            stateService.clearAuthState(chatId);
            log.error("Error checking user existence: {}", e.getMessage());
            return new SendMessage(chatId, AUTH_ERROR).parseMode(ParseMode.HTML);
        }
    }

    private SendMessage processPasswordInput(long chatId, String password) {
        password = password.trim();

        if (password.isEmpty()) {
            return new SendMessage(chatId, AUTH_EMPTY_PASSWORD).parseMode(ParseMode.HTML);
        }

        TelegramStateService.AuthData authData = stateService.getAuthData(chatId);

        if (authData == null || authData.isExpired()) {
            stateService.clearAuthState(chatId);
            return new SendMessage(chatId, AUTH_SESSION_EXPIRED).parseMode(ParseMode.HTML);
        }

        String login = authData.getLogin();

        try {
            TelegramStateService.AuthResult authResult = stateService.authenticate(login, password);

            if (authResult.isSuccess()) {
                var account = authResult.getAccount();
                if (account instanceof Account) {
                  Account userAccount =
                            (Account) account;

                    stateService.linkTelegramAccount(chatId,
                            userAccount.getId(),
                            authData.getNickname());
                }

                stateService.saveUserToken(chatId, authResult.getToken());

                // Удаляем все сообщения с паролями после успешной авторизации
                cleanupPasswordMessages(chatId);

                stateService.clearAuthState(chatId);

                log.info("Authentication successful for chatId: {}, user: {}",
                        chatId, authData.getNickname());

                return new SendMessage(chatId,
                        String.format(AUTH_SUCCESS, authData.getNickname()) +
                                "\n\n" + AUTH_SUGGEST_SUBSCRIPTION)
                        .parseMode(ParseMode.HTML);

            } else {
                stateService.clearAuthState(chatId);
                return new SendMessage(chatId, AUTH_WRONG_PASSWORD).parseMode(ParseMode.HTML);
            }

        } catch (Exception e) {
            stateService.clearAuthState(chatId);
            log.error("Error during authentication: {}", e.getMessage());
            return new SendMessage(chatId, AUTH_FAILED).parseMode(ParseMode.HTML);
        }
    }

    /**
     * Регистрирует сообщение с паролем для автоматического удаления
     * Этот метод должен вызываться из основного обработчика сообщений
     */
    public void registerPasswordMessageForDeletion(long chatId, int messageId) {
        TelegramStateService.UserAuthState state = stateService.getAuthState(chatId);

        if (state == TelegramStateService.UserAuthState.WAITING_PASSWORD) {
            log.info("Registering password message for deletion: chatId={}, messageId={}",
                    chatId, messageId);

            // Сохраняем сообщение для удаления
            stateService.addPasswordMessageToDelete(chatId, messageId);

            // Запланировать удаление через N секунд
            telegramBotService.deleteMessageWithDelay(chatId, messageId,
                    PASSWORD_DELETE_DELAY_SECONDS);
        }
    }

    /**
     * Удаляет все сообщения с паролями для указанного чата
     */
    public void cleanupPasswordMessages(long chatId) {
        List<Integer> messagesToDelete = stateService.getPasswordMessagesToDelete(chatId);

        if (!messagesToDelete.isEmpty()) {
            log.info("Cleaning up {} password messages for chatId: {}",
                    messagesToDelete.size(), chatId);

            telegramBotService.deleteMessagesWithDelay(chatId, messagesToDelete, 1);
        }

        stateService.clearPasswordMessages(chatId);
    }

}