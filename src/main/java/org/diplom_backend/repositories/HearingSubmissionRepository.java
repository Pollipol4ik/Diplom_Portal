package org.diplom_backend.repositories;

import org.diplom_backend.model.HearingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HearingSubmissionRepository extends JpaRepository<HearingSubmission, Long> {

    @Query("SELECT DISTINCT s FROM HearingSubmission s LEFT JOIN FETCH s.reviews WHERE s.id = :id")
    Optional<HearingSubmission> findByIdWithReviews(@Param("id") Long id);

    Optional<HearingSubmission> findByLesson_IdAndGroup_Id(Long lessonId, Long groupId);

    List<HearingSubmission> findByLesson_Id(Long lessonId);

    @Query("SELECT DISTINCT s FROM HearingSubmission s LEFT JOIN FETCH s.reviews WHERE s.lesson.id = :lessonId")
    List<HearingSubmission> findByLesson_IdWithReviews(@Param("lessonId") Long lessonId);

    @Query("SELECT DISTINCT s FROM HearingSubmission s LEFT JOIN FETCH s.reviews WHERE s.lesson.id = :lessonId AND s.group.id = :groupId")
    Optional<HearingSubmission> findByLesson_IdAndGroup_IdWithReviews(@Param("lessonId") Long lessonId,
                                                                      @Param("groupId") Long groupId);

    List<HearingSubmission> findByGroup_Id(Long groupId);

    @Query("""
            SELECT DISTINCT hs
            FROM HearingSubmission hs
            JOIN FETCH hs.lesson l
            JOIN FETCH hs.group g
            LEFT JOIN FETCH hs.reviews r
            WHERE l.course.id = :courseId
            """)
    List<HearingSubmission> findAllByCourseIdWithReviews(@Param("courseId") Long courseId);
}
