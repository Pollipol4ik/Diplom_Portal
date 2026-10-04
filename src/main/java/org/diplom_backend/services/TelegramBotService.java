package org.diplom_backend.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с Telegram Bot API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramBot bot;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    /**
     * Удаляет сообщение из чата
     */
    public void deleteMessage(long chatId, int messageId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
            var response = bot.execute(deleteMessage);

            if (response.isOk()) {
                log.debug("Успешно удалено сообщение {} из чата {}", messageId, chatId);
            } else {
                log.warn("Не удалось удалить сообщение {} из чата {}: {}",
                        messageId, chatId, response.description());
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении сообщения {} из чата {}: {}",
                    messageId, chatId, e.getMessage());
        }
    }

    /**
     * Удаляет несколько сообщений с задержкой
     */
    public void deleteMessagesWithDelay(long chatId, List<Integer> messageIds, int delaySeconds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return;
        }

        scheduler.schedule(() -> {
            for (Integer messageId : messageIds) {
                if (messageId != null) {
                    deleteMessage(chatId, messageId);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, delaySeconds, TimeUnit.SECONDS);

        log.info("Запланировано удаление {} сообщений в чате {} через {} секунд",
                messageIds.size(), chatId, delaySeconds);
    }

    /**
     * Удаляет одно сообщение с задержкой
     */
    public void deleteMessageWithDelay(long chatId, int messageId, int delaySeconds) {
        scheduler.schedule(() -> deleteMessage(chatId, messageId),
                delaySeconds, TimeUnit.SECONDS);

        log.debug("Запланировано удаление сообщения {} в чате {} через {} секунд",
                messageId, chatId, delaySeconds);
    }

    /**
     * Отправляет текстовое сообщение в чат с HTML-разметкой
     *
     * @param chatId  ID чата
     * @param message Текст сообщения
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendMessage(long chatId, String message) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, message)
                    .parseMode(ParseMode.HTML);

            SendResponse response = bot.execute(sendMessage);

            if (response.isOk()) {
                log.debug("Успешно отправлено сообщение в чат {}", chatId);
                return response.message().messageId();
            } else {
                log.warn("Не удалось отправить сообщение в чат {}: {}",
                        chatId, response.description());
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения в чат {}: {}",
                    chatId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Отправляет текстовое сообщение без HTML-разметки
     *
     * @param chatId  ID чата
     * @param message Текст сообщения
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendPlainMessage(long chatId, String message) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, message);

            SendResponse response = bot.execute(sendMessage);

            if (response.isOk()) {
                log.debug("Успешно отправлено простое сообщение в чат {}", chatId);
                return response.message().messageId();
            } else {
                log.warn("Не удалось отправить простое сообщение в чат {}: {}",
                        chatId, response.description());
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке простого сообщения в чат {}: {}",
                    chatId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Отправляет сообщение с Markdown-разметкой
     *
     * @param chatId  ID чата
     * @param message Текст сообщения с Markdown
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendMarkdownMessage(long chatId, String message) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, message)
                    .parseMode(ParseMode.Markdown);

            SendResponse response = bot.execute(sendMessage);

            if (response.isOk()) {
                log.debug("Успешно отправлено Markdown сообщение в чат {}", chatId);
                return response.message().messageId();
            } else {
                log.warn("Не удалось отправить Markdown сообщение в чат {}: {}",
                        chatId, response.description());
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке Markdown сообщения в чат {}: {}",
                    chatId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Отправляет сообщение с включенным предпросмотром ссылок
     *
     * @param chatId  ID чата
     * @param message Текст сообщения
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendMessageWithPreview(long chatId, String message) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, message)
                    .parseMode(ParseMode.HTML);

            SendResponse response = bot.execute(sendMessage);

            if (response.isOk()) {
                log.debug("Успешно отправлено сообщение с предпросмотром в чат {}", chatId);
                return response.message().messageId();
            } else {
                log.warn("Не удалось отправить сообщение с предпросмотром в чат {}: {}",
                        chatId, response.description());
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения с предпросмотром в чат {}: {}",
                    chatId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Отправляет уведомление с заголовком и текстом
     *
     * @param chatId  ID чата
     * @param title   Заголовок уведомления
     * @param message Текст уведомления
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendNotification(long chatId, String title, String message) {
        String formattedMessage = String.format(
                "<b>%s</b>\n\n%s",
                escapeHtml(title),
                escapeHtml(message)
        );
        return sendMessage(chatId, formattedMessage);
    }

    /**
     * Отправляет категоризированное уведомление
     *
     * @param chatId   ID чата
     * @param category Категория
     * @param title    Заголовок
     * @param message  Текст сообщения
     * @return ID отправленного сообщения или null в случае ошибки
     */
    public Integer sendCategorizedNotification(long chatId, String category, String title, String message) {
        String formattedMessage = String.format(
                "%s\n\n<b>%s</b>\n\n%s",
                escapeHtml(category),
                escapeHtml(title),
                escapeHtml(message)
        );
        return sendMessage(chatId, formattedMessage);
    }

    /**
     * Массово отправляет категоризированные уведомления в несколько чатов
     *
     * @param chatIds Список ID чатов
     * @param category Категория
     * @param title Заголовок
     * @param message Текст сообщения
     * @return Количество успешно отправленных сообщений
     */
    public int sendCategorizedNotificationToMultipleChats(List<Long> chatIds, String category,
                                                          String title, String message) {
        if (chatIds == null || chatIds.isEmpty()) {
            log.warn("Попытка отправки уведомлений в пустой список чатов");
            return 0;
        }

        // НЕ экранируем, так как sendMessage уже экранирует или использует HTML режим
        String formattedMessage;
        if (title != null && !title.isEmpty()) {
            formattedMessage = String.format(
                    "%s\n\n%s\n\n%s",
                    category,
                    title,
                    message != null ? message : ""
            );
        } else {
            formattedMessage = String.format(
                    "%s\n\n%s",
                    category,
                    message != null ? message : ""
            );
        }

        return sendBulkMessages(chatIds, formattedMessage);
    }

    /**
     * Массово отправляет сообщения в несколько чатов
     *
     * @param chatIds Список ID чатов
     * @param message Текст сообщения
     * @return Количество успешно отправленных сообщений
     */
    public int sendBulkMessages(List<Long> chatIds, String message) {
        if (chatIds == null || chatIds.isEmpty()) {
            log.warn("Попытка массовой отправки в пустой список чатов");
            return 0;
        }

        int successCount = 0;
        int totalChats = chatIds.size();

        log.info("Начата массовая рассылка в {} чатов", totalChats);

        for (Long chatId : chatIds) {
            try {
                Integer messageId = sendMessage(chatId, message);
                if (messageId != null) {
                    successCount++;
                }

                if (successCount % 30 == 0) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(35);
                }

            } catch (InterruptedException e) {
                log.error("Прервана отправка сообщений: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в чат {}: {}",
                        chatId, e.getMessage());
            }
        }

        log.info("Массовая рассылка завершена: отправлено {}/{} сообщений",
                successCount, totalChats);

        return successCount;
    }

    /**
     * Асинхронно отправляет сообщения в несколько чатов.
     * Запускает рассылку в отдельном потоке через планировщик.
     *
     * @param chatIds Список ID чатов
     * @param message Текст сообщения
     */
    public void sendBulkMessagesAsync(List<Long> chatIds, String message) {
        if (chatIds == null || chatIds.isEmpty()) {
            log.warn("Попытка асинхронной массовой отправки в пустой список чатов");
            return;
        }

        scheduler.execute(() -> {
            log.info("Начата асинхронная рассылка в {} чатов", chatIds.size());
            sendBulkMessages(chatIds, message);
        });
    }

    /**
     * Отправляет сообщение с задержкой
     *
     * @param chatId       ID чата
     * @param message      Текст сообщения
     * @param delaySeconds Задержка в секундах
     */
    public void sendMessageWithDelay(long chatId, String message, int delaySeconds) {
        scheduler.schedule(() -> {
            sendMessage(chatId, message);
        }, delaySeconds, TimeUnit.SECONDS);

        log.debug("Запланирована отправка сообщения в чат {} через {} секунд",
                chatId, delaySeconds);
    }

    /**
     * Проверяет доступность бота
     *
     * @return true если бот доступен, false в противном случае
     */
    public boolean isBotAvailable() {
        try {
            var response = bot.execute(new com.pengrad.telegrambot.request.GetMe());
            return response.isOk();
        } catch (Exception e) {
            log.error("Ошибка при проверке доступности бота: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Получает информацию о боте
     *
     * @return Username бота или null в случае ошибки
     */
    public String getBotUsername() {
        try {
            var response = bot.execute(new com.pengrad.telegrambot.request.GetMe());
            if (response.isOk() && response.user() != null) {
                return response.user().username();
            }
        } catch (Exception e) {
            log.error("Ошибка при получении информации о боте: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Экранирует специальные символы HTML
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /**
     * Завершает работу планировщика задач
     * Вызывается при остановке приложения
     */
    @PreDestroy
    public void shutdown() {
        log.info("Завершение работы планировщика задач TelegramBotService");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                log.warn("Принудительное завершение планировщика задач");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Прервано ожидание завершения планировщика задач");
        }
    }
}