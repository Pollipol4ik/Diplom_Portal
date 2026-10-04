package org.diplom_backend.commands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.diplom_backend.services.TelegramStateService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.diplom_backend.utils.MessagesUtils.ERROR_MESSAGE;
import static org.diplom_backend.utils.MessagesUtils.ONLY_TEXT_TO_SEND;


@Component
@Log4j2
@RequiredArgsConstructor
public class CommandChain {

    private final Map<String, CommandExecutor> executors = new HashMap<>();
    private final TelegramStateService stateService;

    public void registerCommand(String commandName, CommandExecutor executor) {
        executors.put(commandName, executor);
        log.debug("Registered command: {}", commandName);
    }

    public SendMessage executeCommand(String message, long chatId) {
        if (message == null) {
            log.info("Null message has been received");
            return new SendMessage(chatId, ONLY_TEXT_TO_SEND);
        }

        log.debug("CommandChain processing: '{}' for chatId: {}", message, chatId);

        // Проверяем, является ли сообщение callback-данными (не начинается с /)
        boolean isCallback = !message.startsWith("/");

        // Для callback-данных ищем специальный обработчик
        if (isCallback) {
            CommandExecutor callbackExecutor = findCallbackExecutor(message);
            if (callbackExecutor != null) {
                log.debug("Found callback executor for: {}", message);
                return callbackExecutor.execute(message, chatId);
            }
        }

        // Для обычных команд (/command) пытаемся найти по первому слову
        String[] splitCommand = message.split(" ");
        String firstWord = splitCommand[0];

        CommandExecutor executor = executors.get(firstWord);
        if (executor != null) {
            log.debug("Found executor for command: {}", firstWord);
            return executor.execute(message, chatId);
        }

        // Проверяем состояние пользователя
        TelegramStateService.UserAuthState currentState = stateService.getAuthState(chatId);

        // Если идет процесс авторизации
        if (currentState == TelegramStateService.UserAuthState.WAITING_LOGIN
                || currentState == TelegramStateService.UserAuthState.WAITING_PASSWORD) {
            CommandExecutor authExecutor = executors.get("/auth");
            if (authExecutor != null) {
                log.debug("Redirecting to auth executor for state: {}", currentState);
                return authExecutor.execute(message, chatId);
            }
        }

        // Если идет процесс подписки
        if (isSubscriptionState(currentState)) {
            CommandExecutor subscribeExecutor = executors.get("/subscribe");
            if (subscribeExecutor != null) {
                log.debug("Redirecting to subscribe executor for state: {}", currentState);
                return subscribeExecutor.execute(message, chatId);
            }
        }

        log.warn("No executor found for message: '{}', chatId: {}", message, chatId);
        return new SendMessage(chatId, ERROR_MESSAGE).parseMode(ParseMode.HTML);
    }

    /**
     * Находит обработчик для callback-данных
     */
    private CommandExecutor findCallbackExecutor(String callbackData) {
        if (callbackData == null) return null;

        // Подписки
        if (callbackData.startsWith("subtype:")
                || callbackData.startsWith("course:")
                || callbackData.startsWith("subject:")
                || callbackData.startsWith("topic:")
                || callbackData.startsWith("subact:")
                || callbackData.startsWith("topicact:")
                || callbackData.startsWith("finish:")
                || "back".equals(callbackData)
                || "cancel".equals(callbackData)
                || "ignore".equals(callbackData)) {
            return executors.get("/subscribe");
        }

        // Отписки
        if (callbackData.startsWith("unsub:")
                || callbackData.startsWith("confirm_unsub:")
                || "back_unsub".equals(callbackData)
                || "cancel_unsub".equals(callbackData)
                || "confirm_unsub_all".equals(callbackData)
                || "unsub_all".equals(callbackData)) {
            return executors.get("/unsubscribe");
        }

        // Авторизация
        if (callbackData.startsWith("auth:")) {
            return executors.get("/auth");
        }
        if (callbackData.startsWith("auth:logout")) {
            return executors.get("/logout");
        }

        return null;
    }

    private boolean isSubscriptionState(TelegramStateService.UserAuthState state) {
        return state == TelegramStateService.UserAuthState.WAITING_SUBSCRIBE_TYPE
                || state == TelegramStateService.UserAuthState.WAITING_COURSE_CHOICE
                || state == TelegramStateService.UserAuthState.WAITING_SUBJECT_ACTION
                || state == TelegramStateService.UserAuthState.WAITING_TOPIC_ACTION
                || state == TelegramStateService.UserAuthState.WAITING_SUBJECT_CHOICE
                || state == TelegramStateService.UserAuthState.WAITING_TOPIC_CHOICE;
    }
}