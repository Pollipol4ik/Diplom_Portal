package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_school",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "school_id")
    )
    @Builder.Default
    private Set<School> schools = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** Курс поддержки отстающих: автоматическое создание группы для учеников с is_lagging по школам курса */
    @Column(name = "for_lagging_students", nullable = false)
    @Builder.Default
    private Boolean forLaggingStudents = false;

    /**
     * Вводный курс: виден ВСЕМ ученикам школы (и обычным, и отстающим).
     * Используется для ознакомления до распределения на целевые курсы.
     */
    @Column(name = "is_introduction", nullable = false)
    @Builder.Default
    private Boolean isIntroduction = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNumber ASC")
    @Builder.Default
    private List<CourseLessonEntity> lessons = new ArrayList<>();
}