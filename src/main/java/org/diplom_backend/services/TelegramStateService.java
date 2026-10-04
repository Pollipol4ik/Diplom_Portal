package org.diplom_backend.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.TelegramAccountEntity;
import org.diplom_backend.repositories.TelegramAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramStateService {

    private final AccountService accountService;
    private final AuthService authService;
    private final TelegramAccountRepository telegramAccountRepository;

    private final Map<Long, UserAuthState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, AuthData> authDataCache = new ConcurrentHashMap<>();
    private final Map<Long, TelegramUser> authenticatedUsers = new ConcurrentHashMap<>();
    private final Map<Long, SubscribeData> subscribeDataCache = new ConcurrentHashMap<>();


    public enum UserAuthState {
        WAITING_LOGIN,
        WAITING_PASSWORD,
        WAITING_SUBSCRIBE_TYPE,
        WAITING_COURSE_CHOICE,
        WAITING_SUBJECT_ACTION,
        WAITING_TOPIC_ACTION,
        WAITING_SUBJECT_CHOICE,
        WAITING_TOPIC_CHOICE,
        NONE
    }

    @Data
    @AllArgsConstructor
    public static class SubscribeData {
        private Long courseId;
        private Long subjectId;
        private Long topicId;
        private boolean subscribeToNews;
        private boolean subscribeToCourse;
        private boolean subscribeToSubject;
        private boolean subscribeToTopic;

        public SubscribeData() {
            this.subscribeToNews = false;
            this.subscribeToCourse = false;
            this.subscribeToSubject = false;
            this.subscribeToTopic = false;
        }
    }

    @Data
    @AllArgsConstructor
    public static class AuthData {
        private String login;
        private String nickname;
        private long timestamp;
        private List<Integer> passwordMessagesToDelete;

        public AuthData(String login, String nickname) {
            this.login = login;
            this.nickname = nickname;
            this.timestamp = System.currentTimeMillis();
            this.passwordMessagesToDelete = new CopyOnWriteArrayList<>();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 5 * 60 * 1000;
        }

        public void addPasswordMessageToDelete(int messageId) {
            this.passwordMessagesToDelete.add(messageId);
        }

        public void clearPasswordMessages() {
            this.passwordMessagesToDelete.clear();
        }
    }

    @Data
    @AllArgsConstructor
    public static class TelegramUser {
        private Long accountId;
        private String nickname;
        private String authToken;
        private boolean subscribed;
    }

    @Data
    @AllArgsConstructor
    public static class AuthResult {
        private boolean success;
        private Object account;
        private String token;
    }

    /**
     * Проверка авторизации пользователя с учетом БД
     */
    @Transactional
    public boolean isUserAuthenticated(long chatId) {
        if (authenticatedUsers.containsKey(chatId)) {
            return true;
        }

        Optional<TelegramAccountEntity> telegramAccount =
                telegramAccountRepository.findByChatId(chatId);

        if (telegramAccount.isPresent()) {
            TelegramAccountEntity entity = telegramAccount.get();
            TelegramUser user = new TelegramUser(
                    entity.getAccount().getId(),
                    entity.getAccount().getNickname(),
                    entity.getAuthToken(),
                    false
            );
            authenticatedUsers.put(chatId, user);


            entity.setLastActive(LocalDateTime.now());
            telegramAccountRepository.save(entity);

            log.info("Restored session from DB for chatId: {}, user: {}",
                    chatId, entity.getAccount().getNickname());
            return true;
        }

        return false;
    }

    /**
     * Связывает Telegram-аккаунт с системным аккаунтом
     */
    @Transactional
    public void linkTelegramAccount(long chatId, Long accountId, String nickname) {
        try {
            Account account = accountService.getAccountById(accountId);
            TelegramUser user = new TelegramUser(accountId, nickname, null, false);
            authenticatedUsers.put(chatId, user);

            Optional<TelegramAccountEntity> existing = telegramAccountRepository.findByChatId(chatId);

            TelegramAccountEntity entity;
            if (existing.isPresent()) {
                entity = existing.get();
                entity.setAccount(account);
            } else {
                entity = new TelegramAccountEntity();
                entity.setChatId(chatId);
                entity.setAccount(account);
                entity.setCreatedAt(LocalDateTime.now());
            }
            entity.setLastActive(LocalDateTime.now());
            telegramAccountRepository.save(entity);

            log.info("Linked telegram account: chatId={}, accountId={}, nickname={}",
                    chatId, accountId, nickname);
        } catch (EntityModelNotFoundException e) {
            log.error("Account not found for linking: id={}, chatId={}", accountId, chatId);

            TelegramUser user = new TelegramUser(accountId, nickname, null, false);
            authenticatedUsers.put(chatId, user);
        } catch (Exception e) {
            log.error("Error linking telegram account: {}", e.getMessage(), e);

            TelegramUser user = new TelegramUser(accountId, nickname, null, false);
            authenticatedUsers.put(chatId, user);
        }
    }

    /**
     * Сохраняет токен пользователя в БД
     */
    @Transactional
    public void saveUserToken(long chatId, String token) {
        TelegramUser user = authenticatedUsers.get(chatId);
        if (user != null) {
            user.setAuthToken(token);

            Optional<TelegramAccountEntity> entity =
                    telegramAccountRepository.findByChatId(chatId);
            entity.ifPresent(e -> {
                e.setAuthToken(token);
                telegramAccountRepository.save(e);
            });
        }
    }

    /**
     * Разлогинивает пользователя
     */
    @Transactional
    public void logoutUser(long chatId) {
        authenticatedUsers.remove(chatId);
        clearAuthState(chatId);

        telegramAccountRepository.deleteByChatId(chatId);
        log.info("User logged out: chatId={}", chatId);
    }

    // ========== МЕТОДЫ ДЛЯ УПРАВЛЕНИЯ СООБЩЕНИЯМИ С ПАРОЛЯМИ ==========

    /**
     * Добавляет сообщение с паролем для удаления
     */
    public void addPasswordMessageToDelete(long chatId, int messageId) {
        AuthData authData = getAuthData(chatId);
        if (authData != null) {
            authData.addPasswordMessageToDelete(messageId);
        }
    }

    /**
     * Получает все сообщения с паролями для удаления
     */
    public List<Integer> getPasswordMessagesToDelete(long chatId) {
        AuthData authData = getAuthData(chatId);
        return authData != null ? authData.getPasswordMessagesToDelete() : new ArrayList<>();
    }

    /**
     * Очищает список сообщений с паролями
     */
    public void clearPasswordMessages(long chatId) {
        AuthData authData = getAuthData(chatId);
        if (authData != null) {
            authData.clearPasswordMessages();
        }
    }


    public void setAuthState(long chatId, UserAuthState state) {
        userStates.put(chatId, state);
    }

    public UserAuthState getAuthState(long chatId) {
        return userStates.getOrDefault(chatId, UserAuthState.NONE);
    }

    public void saveAuthData(long chatId, String login, String nickname) {
        AuthData data = new AuthData(login, nickname);
        authDataCache.put(chatId, data);
    }

    public AuthData getAuthData(long chatId) {
        return authDataCache.get(chatId);
    }

    public String getUserNickname(long chatId) {
        TelegramUser user = authenticatedUsers.get(chatId);
        return user != null ? user.getNickname() : null;
    }

    public TelegramUser getAuthenticatedUser(long chatId) {
        return authenticatedUsers.get(chatId);
    }

    public void saveSubscribeData(long chatId, SubscribeData data) {
        subscribeDataCache.put(chatId, data);
    }

    public SubscribeData getSubscribeData(long chatId) {
        return subscribeDataCache.get(chatId);
    }

    public void clearSubscribeData(long chatId) {
        subscribeDataCache.remove(chatId);
    }

    public void clearAuthState(long chatId) {
        userStates.remove(chatId);
        authDataCache.remove(chatId);
        subscribeDataCache.remove(chatId);
        clearPasswordMessages(chatId);
    }

    public void subscribeUser(long chatId) {
        TelegramUser user = authenticatedUsers.get(chatId);
        if (user != null) {
            user.setSubscribed(true);
        }
    }

    public void unsubscribeUser(long chatId) {
        TelegramUser user = authenticatedUsers.get(chatId);
        if (user != null) {
            user.setSubscribed(false);
        }
    }

    public boolean isUserSubscribed(long chatId) {
        TelegramUser user = authenticatedUsers.get(chatId);
        return user != null && user.isSubscribed();
    }

    public List<Long> getAllSubscribedChatIds() {
        return authenticatedUsers.entrySet().stream()
                .filter(entry -> entry.getValue().isSubscribed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<TelegramUser> getUsersByAccountId(Long accountId) {
        return authenticatedUsers.values().stream()
                .filter(user -> accountId.equals(user.getAccountId()))
                .collect(Collectors.toList());
    }

    public boolean userExists(String login) {
        try {
            accountService.getAccountByEmail(login);
            return true;
        } catch (EntityModelNotFoundException e1) {
            try {
                accountService.getAccount(login);
                return true;
            } catch (EntityModelNotFoundException e2) {
                return false;
            }
        }
    }

    public String getUserNicknameByLogin(String login) {
        try {
            return accountService.getAccountByEmail(login).getNickname();
        } catch (EntityModelNotFoundException e1) {
            try {
                return accountService.getAccount(login).getNickname();
            } catch (EntityModelNotFoundException e2) {
                throw new RuntimeException("Пользователь не найден: " + login);
            }
        }
    }

    public AuthResult authenticate(String login, String password) {
        try {
            var authResult = authService.login(login, password);
            if (authResult != null && authResult.getFirst() != null) {
                var account = authResult.getFirst();
                return new AuthResult(true, account, authResult.getSecond());
            }
            return new AuthResult(false, null, null);
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            return new AuthResult(false, null, null);
        }
    }
}
