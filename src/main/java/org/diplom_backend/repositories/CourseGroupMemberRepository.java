package org.diplom_backend.repositories;

import org.diplom_backend.model.CourseGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGroupMemberRepository extends JpaRepository<CourseGroupMember, Long> {

    List<CourseGroupMember> findByGroup_Id(Long groupId);

    Optional<CourseGroupMember> findByGroup_IdAndAccount_Id(Long groupId, Long accountId);

    boolean existsByGroup_IdAndAccount_Id(Long groupId, Long accountId);

    @Query("SELECT m FROM CourseGroupMember m WHERE m.account.id = :accountId AND m.group.course.id = :courseId")
    Optional<CourseGroupMember> findByAccountAndCourse(@Param("accountId") Long accountId,
                                                       @Param("courseId") Long courseId);

    boolean existsByAccount_IdAndGroup_School_Id(Long accountId, Long schoolId);

    @Query("SELECT m FROM CourseGroupMember m WHERE m.account.id = :accountId")
    List<CourseGroupMember> findAllByAccount(@Param("accountId") Long accountId);

    @Query("""
            SELECT m
            FROM CourseGroupMember m
            JOIN FETCH m.account a
            JOIN FETCH m.group g
            WHERE g.course.id = :courseId
            """)
    List<CourseGroupMember> findAllByCourseId(@Param("courseId") Long courseId);

    void deleteByGroup_IdAndAccount_Id(Long groupId, Long accountId);
}