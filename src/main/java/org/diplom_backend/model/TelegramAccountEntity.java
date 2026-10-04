package org.diplom_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Сущность для хранения связи между Telegram чатом и аккаунтом пользователя
 */
@Entity
@Table(name = "telegram_account")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TelegramAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID чата в Telegram
     */
    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    /**
     * Аккаунт пользователя
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    /**
     * Токен авторизации
     */
    @Column(name = "auth_token", length = 512)
    private String authToken;

    /**
     * Дата создания связи
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Последняя активность
     */
    @Column(name = "last_active")
    private LocalDateTime lastActive;
}
