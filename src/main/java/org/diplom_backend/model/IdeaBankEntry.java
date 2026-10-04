package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Запись в банке идей — архивная тема проекта,
 * сохранённая для предложения другим ученикам.
 */
@Entity
@Table(name = "idea_bank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdeaBankEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "score", nullable = false)
    @Builder.Default
    private Integer score = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Account createdBy;

    @Column(name = "source_project_id")
    private Long sourceProjectId;

    /** Курс, к которому относится идея (опционально, проставляется при архивировании). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}