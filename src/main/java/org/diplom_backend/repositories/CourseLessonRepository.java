package org.diplom_backend.repositories;

import org.diplom_backend.model.CourseLessonEntity;
import org.diplom_backend.model.HearingStage;
import org.diplom_backend.model.LessonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLessonEntity, Long> {

    List<CourseLessonEntity> findByCourse_IdOrderByOrderNumberAsc(Long courseId);

    List<CourseLessonEntity> findByCourse_IdAndCategoryOrderByOrderNumberAsc(Long courseId,
                                                                             LessonCategory category);

    boolean existsByCourse_IdAndOrderNumber(Long courseId, Integer orderNumber);

    List<CourseLessonEntity> findByCategoryAndHearingStage(LessonCategory category, HearingStage hearingStage);

    @Query("SELECT DISTINCT l FROM CourseLessonEntity l JOIN FETCH l.course c LEFT JOIN FETCH c.schools " +
            "WHERE l.category = :category AND l.hearingStage = :stage")
    List<CourseLessonEntity> findByCategoryAndHearingStageWithCourseSchools(
            @Param("category") LessonCategory category,
            @Param("stage") HearingStage stage);
}