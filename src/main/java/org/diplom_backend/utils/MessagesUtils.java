package org.diplom_backend.utils;

import lombok.experimental.UtilityClass;

import static org.diplom_backend.commands.Command.AUTH;
import static org.diplom_backend.commands.Command.HELP;
import static org.diplom_backend.commands.Command.LOGOUT;
import static org.diplom_backend.commands.Command.START;
import static org.diplom_backend.commands.Command.SUB;
import static org.diplom_backend.commands.Command.UNSUB;

/**
 * Все текстовые сообщения Telegram-бота платформы МосПолитех «ИТ-класс».
 * <p>
 * Система: платформа проектной деятельности для учеников ИТ-классов московских школ.
 * Ученики выбирают темы проектов, проходят этапы слушаний (тема → промежуточный показ → защита).
 * Модераторы (преподаватели) проверяют работы и оставляют рецензии.
 * Бот уведомляет о новых публикациях, сданных работах и результатах проверки.
 */
@UtilityClass
public class MessagesUtils {

    // ─── Приветствие ───────────────────────────────────────────────────────────

    public static final String WELCOME_MESSAGE = """
            👋 <b>Добро пожаловать в бот Московского Политехнического Университета!</b>

            🎓 Этот бот — часть платформы <b>«ДоВуз»</b>.

            <b>Что умеет бот:</b>
            🔔 Уведомлять об обновлениях методических публикаций
            📋 Сообщать о результатах проверки ваших проектных работ
            👨‍🏫 Оповещать модераторов о новых сдачах работ учениками
            📚 Управлять подписками на предметы, темы и курсы

            <b>Как начать:</b>
            1️⃣ Авторизуйтесь командой /auth
            2️⃣ Выберите подписки через /subscribe
            3️⃣ Получайте уведомления в реальном времени

            ℹ️ Полный список команд: /help
            """;

    // ─── Справка ───────────────────────────────────────────────────────────────

    public static final String HELP_MESSAGE = """
            <b>📋 Доступные команды:</b>

            /start — запустить бота и увидеть приветствие
            /auth — привязать аккаунт платформы к Telegram
            /subscribe — подписаться на уведомления о публикациях
            /unsubscribe — отписаться от уведомлений
            /logout — отвязать аккаунт от Telegram
            /help — показать эту справку

            <b>💡 Как работают уведомления:</b>
            • <b>Ученики</b> получают сообщения о проверке своих работ (статус, оценка, комментарий преподавателя)
            • <b>Модераторы</b> получают оповещения о новых сданных работах учеников
            • <b>Все подписчики</b> получают уведомления о новых методических публикациях

            <b>📌 О платформе:</b>
            Платформа предназначена для проектной деятельности учеников ИТ-классов в рамках программы «ИТ-класс в московской школе».
            Ученики выбирают тему проекта, проходят этапы согласования темы, промежуточного показа и финальной защиты.
            """;

    // ─── Авторизация ───────────────────────────────────────────────────────────

    public static final String AUTH_REQUEST_LOGIN =
            "🔐 <b>Авторизация на платформе МосПолитех</b>\n\n" +
                    "Введите ваш <b>email или никнейм</b>, который вы используете на платформе:\n\n" +
                    "Пример: <code>ivanov@school.ru</code>\n" +
                    "или: <code>ivan_ivanov</code>\n\n" +
                    "❓ Если у вас нет аккаунта — зарегистрируйтесь на сайте платформы.";

    public static final String AUTH_REQUEST_PASSWORD =
            "✅ Логин найден!\n\n" +
                    "Введите ваш <b>пароль</b>:\n\n" +
                    "🔒 Сообщение с паролем будет автоматически удалено из истории чата.";

    public static final String AUTH_SUCCESS =
            "🎉 <b>Авторизация успешна!</b>\n\n" +
                    "Добро пожаловать, <b>%s</b>!\n" +
                    "Ваш аккаунт платформы привязан к Telegram.\n\n" +
                    "Теперь вы будете получать уведомления об обновлениях.";

    public static final String AUTH_SUGGEST_SUBSCRIPTION =
            "\n\nХотите получать уведомления о публикациях по вашим курсам и предметам?\n" +
                    "Настройте подписки командой: /subscribe";

    public static final String AUTH_ALREADY_AUTHENTICATED =
            "ℹ️ Вы уже авторизованы как <b>%s</b>.\n\n" +
                    "Для смены аккаунта сначала выйдите командой /logout.";

    public static final String AUTH_EMPTY_LOGIN =
            "❌ Логин не может быть пустым.\n\n" +
                    "Введите ваш email или никнейм с платформы МосПолитех.";

    public static final String AUTH_EMPTY_PASSWORD =
            "❌ Пароль не может быть пустым.\n\n" +
                    "Введите пароль от вашего аккаунта на платформе.";

    public static final String AUTH_USER_NOT_FOUND =
            "❌ Пользователь <b>%s</b> не найден на платформе.\n\n" +
                    "Проверьте правильность email или никнейма.\n" +
                    "Регистрация доступна на сайте платформы.";

    public static final String AUTH_WRONG_PASSWORD =
            "❌ Неверный пароль.\n\n" +
                    "Попробуйте ещё раз: /auth\n" +
                    "Если забыли пароль — обратитесь к администратору платформы.";

    public static final String AUTH_SESSION_EXPIRED =
            "⚠️ Сессия авторизации устарела.\n\n" +
                    "Начните заново: /auth";

    public static final String AUTH_FAILED =
            "❌ Не удалось выполнить авторизацию.\n\n" +
                    "Попробуйте позже: /auth\n" +
                    "Если проблема повторяется — обратитесь к администратору.";

    public static final String AUTH_ERROR =
            "❌ Произошла ошибка при авторизации.\n\n" +
                    "Попробуйте ещё раз: /auth";

    // ─── Выход ─────────────────────────────────────────────────────────────────

    public static final String LOGOUT_SUCCESS =
            "👋 <b>Вы вышли из аккаунта.</b>\n\n" +
                    "Уведомления больше не будут приходить в этот чат.\n" +
                    "Для повторной авторизации используйте: /auth";

    public static final String LOGOUT_NOT_AUTHENTICATED =
            "ℹ️ Вы не авторизованы.\n\n" +
                    "Войдите в аккаунт командой: /auth";

    // ─── Подписки ──────────────────────────────────────────────────────────────

    public static final String SUBSCRIBE_TYPE_MENU =
            "🔔 <b>Настройка подписок на уведомления</b>\n\n" +
                    "Выберите, о чём хотите получать уведомления:\n\n" +
                    "• <b>Курс</b> — все публикации по выбранному курсу\n" +
                    "• <b>Предмет</b> — публикации по конкретному предмету\n" +
                    "• <b>Тема</b> — публикации по определённой теме предмета";

    public static final String SUBSCRIBE_COURSES_LIST =
            "📚 <b>Выберите курс для подписки:</b>\n\n" +
                    "Вы получите уведомления о всех новых публикациях в выбранном курсе.";

    public static final String SUBSCRIBE_SUBJECTS_LIST =
            "📖 <b>Выберите предмет для подписки:</b>";

    public static final String SUBSCRIBE_SUCCESS =
            "✅ <b>Подписка оформлена!</b>\n\n" +
                    "Вы будете получать уведомления о новых публикациях.\n" +
                    "Управление подписками: /unsubscribe";

    public static final String SUBSCRIBE_ALREADY_EXISTS =
            "ℹ️ Вы уже подписаны на этот раздел.";

    public static final String UNSUBSCRIBE_SUCCESS =
            "✅ <b>Подписка отменена.</b>\n\n" +
                    "Уведомления по этому разделу больше не будут приходить.\n" +
                    "Управление другими подписками: /unsubscribe";

    public static final String NO_SUBSCRIPTIONS =
            "ℹ️ У вас нет активных подписок.\n\n" +
                    "Настройте подписки командой: /subscribe";

    // ─── Общие ошибки ──────────────────────────────────────────────────────────

    public static final String ERROR_MESSAGE =
            "❌ <b>Неизвестная команда.</b>\n\n" +
                    "Воспользуйтесь /help для просмотра доступных команд.";

    public static final String ONLY_TEXT_TO_SEND =
            "⚠️ Пожалуйста, отправьте текстовое сообщение.";

    public static final String HTTPS_PREFIX = "https://";
    public static final String HTTP_PREFIX = "http://";
    public static final String LINK_SHOULD_STARTS_WITH_HTTP =
            "⚠️ Ссылка должна начинаться с " + HTTPS_PREFIX + " или " + HTTP_PREFIX;

    // ─── Уведомления о проектной деятельности ──────────────────────────────────

    /**
     * Форматирует статус проверки работы на русском языке.
     */
    public static String formatSubmissionStatus(String status) {
        return switch (status) {
            case "ACCEPTED"       -> "✅ Принято";
            case "NEEDS_REVISION" -> "🔄 На доработку";
            case "SUBMITTED"      -> "⏳ На проверке";
            case "REJECTED"       -> "❌ Отклонено";
            case "ON_REVIEW"      -> "⏳ На рассмотрении";
            default               -> status;
        };
    }

    /**
     * Форматирует статус этапа слушания на русском языке.
     */
    public static String formatHearingStatus(String status) {
        return switch (status) {
            case "ACCEPTED"       -> "✅ Принято";
            case "NEEDS_REVISION" -> "🔄 На доработку";
            case "ON_REVIEW"      -> "⏳ На рассмотрении";
            case "REJECTED"       -> "❌ Отклонено";
            default               -> status;
        };
    }
}