package org.diplom_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Сущность подписки пользователя на уведомления
 */
@Entity
@Table(name = "user_subscription",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"account_id", "subscription_type", "direction_id", "subject_id", "topic_id"}
        ))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Аккаунт пользователя
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    /**
     * Тип подписки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false, length = 20)
    private SubscriptionType type;

    /**
     * Номер курса (для подписки на курс)
     */
    @Column(name = "direction_id")
    private Long directionId;

    /**
     * ID предмета (для подписки на предмет)
     */
    @Column(name = "subject_id")
    private Long subjectId;

    /**
     * ID темы (для подписки на тему)
     */
    @Column(name = "topic_id")
    private Long topicId;

    /**
     * Дата создания подписки
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Активна ли подписка
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Типы подписок
     */
    public enum SubscriptionType {
        NEWS,          // Общие новости
        DIRECTION,        // Весь курс
        SUBJECT,       // Весь предмет
        TOPIC          // Конкретная тема
    }
}
