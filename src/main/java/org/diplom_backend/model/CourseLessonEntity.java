package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "course_lesson",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "order_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseLessonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "lecture_content", columnDefinition = "TEXT")
    private String lectureContent;

    @Column(name = "practice_description", columnDefinition = "TEXT")
    private String practiceDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    @Builder.Default
    private LessonCategory category = LessonCategory.LESSON;

    @Enumerated(EnumType.STRING)
    @Column(name = "hearing_stage", length = 30)
    private HearingStage hearingStage;

    /** Для слушаний (этапы 2+): доступно ученикам только после открытия модератором. Этап 1 всегда открыт. */
    @Column(name = "hearing_open_for_students", nullable = false)
    @Builder.Default
    private Boolean hearingOpenForStudents = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false, length = 30)
    @Builder.Default
    private SubmissionType submissionType = SubmissionType.TEXT;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecture_file_id")
    private FileEntity lectureFile;

    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @Column(name = "max_score", nullable = false)
    @Builder.Default
    private Integer maxScore = 100;

    /** Крайний срок сдачи работы (локальное время сервера); null — без ограничения */
    @Column(name = "submission_deadline")
    private LocalDateTime submissionDeadline;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LessonSubmissionEntity> submissions = new ArrayList<>();
}
