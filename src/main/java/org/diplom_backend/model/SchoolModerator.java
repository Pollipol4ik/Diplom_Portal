package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Назначение модератора на школу.
 * Администратор связывает пользователя с ролью ROLE_MODERATOR
 * с конкретной школой. Такой модератор получает права
 * проверять проектные работы учеников этой школы.
 */
@Entity
@Table(name = "school_moderator")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolModerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Аккаунт модератора (должен иметь роль ROLE_MODERATOR). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /** Школа, за которой закреплён модератор. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    /** Дата и время назначения. */
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
}