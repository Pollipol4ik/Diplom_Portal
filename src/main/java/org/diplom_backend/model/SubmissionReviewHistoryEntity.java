package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission_review_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionReviewHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private LessonSubmissionEntity submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_account_id")
    private Account reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_kind", nullable = false, length = 30)
    @Builder.Default
    private SubmissionReviewHistoryKind entryKind = SubmissionReviewHistoryKind.MODERATOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_after", nullable = false, length = 30)
    private SubmissionStatus statusAfter;

    @Column(name = "score")
    private Integer score;

    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    @Column(name = "criteria_snapshot_json", columnDefinition = "TEXT")
    private String criteriaSnapshotJson;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
