package org.diplom_backend.repositories;

import org.diplom_backend.model.CourseModerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModeratorRepository extends JpaRepository<CourseModerator, Long> {

    boolean existsByAccount_IdAndCourse_Id(Long accountId, Long courseId);

    @Query("SELECT DISTINCT sch.id FROM CourseModerator cm JOIN cm.course c JOIN c.schools sch WHERE cm.account.id = :accountId")
    List<Long> findDistinctSchoolIdsForModeratorCourses(@Param("accountId") Long accountId);

    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END FROM CourseModerator cm JOIN cm.course c JOIN c.schools sch WHERE cm.account.id = :accountId AND sch.id = :schoolId")
    boolean existsModeratorLinkedToSchool(@Param("accountId") Long accountId, @Param("schoolId") Long schoolId);

    List<CourseModerator> findAllByCourse_Id(Long courseId);

    List<CourseModerator> findAllByAccount_Id(Long accountId);

    void deleteByAccount_IdAndCourse_Id(Long accountId, Long courseId);

    void deleteByCourse_Id(Long courseId);

}
