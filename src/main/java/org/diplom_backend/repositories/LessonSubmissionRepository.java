package org.diplom_backend.repositories;

import org.diplom_backend.model.LessonSubmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.diplom_backend.model.SubmissionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonSubmissionRepository extends JpaRepository<LessonSubmissionEntity, Long> {

    Optional<LessonSubmissionEntity> findByLesson_IdAndAccount_Id(Long lessonId, Long accountId);

    Page<LessonSubmissionEntity> findByLesson_Id(Long lessonId, Pageable pageable);

    List<LessonSubmissionEntity> findAllByLesson_Id(Long lessonId);

    @Query("SELECT s FROM LessonSubmissionEntity s " +
            "WHERE s.lesson.course.id = :courseId AND s.account.id = :accountId " +
            "ORDER BY s.lesson.orderNumber ASC")
    List<LessonSubmissionEntity> findByCourseAndAccount(
            @Param("courseId") Long courseId,
            @Param("accountId") Long accountId);

    @Query("SELECT s FROM LessonSubmissionEntity s " +
            "WHERE s.lesson.course.id = :courseId " +
            "ORDER BY s.submittedAt DESC")
    Page<LessonSubmissionEntity> findByCourseId(
            @Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT s FROM LessonSubmissionEntity s " +
            "WHERE s.lesson.course.id = :courseId AND s.status = :status " +
            "ORDER BY s.submittedAt DESC")
    Page<LessonSubmissionEntity> findByCourseIdAndStatus(
            @Param("courseId") Long courseId,
            @Param("status") SubmissionStatus status,
            Pageable pageable);

    List<LessonSubmissionEntity> findAllByAccount_Id(Long accountId);

    @Query("SELECT s FROM LessonSubmissionEntity s " +
            "WHERE s.lesson.course.id = :courseId " +
            "ORDER BY s.account.id, s.lesson.orderNumber ASC")
    List<LessonSubmissionEntity> findAllByCourseId(@Param("courseId") Long courseId);
}
