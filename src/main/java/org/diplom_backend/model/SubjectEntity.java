package org.diplom_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Предмет (учебная дисциплина)
 * Автор: Полина Купцова
 */
@Entity
@Table(name = "subject")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubjectEntity {
    /**
     * Идентификатор предмета
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название предмета (хранится в верхнем регистре согласно мапперу)
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Список топиков (тем), относящихся к данному предмету
     */
    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<SubjectTopicEntity> subjectTopics;

    /**
     * Направление (например, Инженерная школа), к которому привязан предмет
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id", referencedColumnName = "id") // Изменено с course_number
    private DirectionEntity direction;
}