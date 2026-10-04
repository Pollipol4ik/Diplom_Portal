package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subject_moderator")
@Getter
@Setter
@NoArgsConstructor // Обязательно для Hibernate
@AllArgsConstructor // Обязательно для @Builder
@Builder // Создает тот самый метод .builder()
public class SubjectModerator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
}