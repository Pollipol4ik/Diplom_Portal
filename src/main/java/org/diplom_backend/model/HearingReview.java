package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hearing_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HearingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private HearingSubmission submission;

    /** Может стать null если модератор удалён из системы. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id", nullable = true)
    private Account moderator;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "submission_version", nullable = false)
    @Builder.Default
    private Integer submissionVersion = 1;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;
}