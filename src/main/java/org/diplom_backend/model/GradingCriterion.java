package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grading_criterion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "order_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private CourseLessonEntity lesson;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_points", nullable = false)
    private Integer maxPoints;
}
