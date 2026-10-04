package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_criterion_grade",
        uniqueConstraints = @UniqueConstraint(columnNames = {"submission_id", "criterion_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionCriterionGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private LessonSubmissionEntity submission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criterion_id", nullable = false)
    private GradingCriterion criterion;

    @Column(name = "points", nullable = false)
    @Builder.Default
    private Integer points = 0;
}
