package org.diplom_backend.repositories;

import org.diplom_backend.model.StudentCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseEnrollmentRepository extends JpaRepository<StudentCourseEnrollment, Long> {

    Optional<StudentCourseEnrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<StudentCourseEnrollment> findAllByStudent_Id(Long studentId);

    List<StudentCourseEnrollment> findAllByCourse_Id(Long courseId);  // ✅ ДОБАВИТЬ

    @Query("SELECT e.course.id FROM StudentCourseEnrollment e WHERE e.student.id = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT e.student.id FROM StudentCourseEnrollment e WHERE e.course.id = :courseId")
    List<Long> findStudentIdsByCourseId(@Param("courseId") Long courseId);  // ✅ ДОБАВИТЬ

    @Modifying
    @Query("DELETE FROM StudentCourseEnrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    void deleteByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Modifying
    @Query("DELETE FROM StudentCourseEnrollment e WHERE e.student.id = :studentId")
    void deleteAllByStudentId(@Param("studentId") Long studentId);

    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}