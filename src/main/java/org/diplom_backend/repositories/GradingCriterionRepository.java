package org.diplom_backend.repositories;

import org.diplom_backend.model.GradingCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingCriterionRepository extends JpaRepository<GradingCriterion, Long> {

    List<GradingCriterion> findByLesson_IdOrderByOrderNumberAsc(Long lessonId);

    boolean existsByLesson_IdAndOrderNumber(Long lessonId, Integer orderNumber);

    void deleteAllByLesson_Id(Long lessonId);
}
