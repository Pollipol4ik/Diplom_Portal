package org.diplom_backend.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Command {
    START("/start", "▶️ Запустить бота"),
    HELP("/help", "ℹ️ Справка по командам"),
    AUTH("/auth", "🔐 Авторизоваться в боте"),
    LOGOUT("/logout", "🚪 Выйти из аккаунта"), // Добавлена команда выхода
    SUB("/subscribe", "🔔 Отслеживать новые публикации"),
    UNSUB("/unsubscribe", "🔕 Отписаться от уведомлений");

    private final String name;
    private final String description;
}