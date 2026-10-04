package org.diplom_backend.repositories;

import org.diplom_backend.model.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Page<CourseEntity> findBySchools_Id(Long schoolId, Pageable pageable);

    List<CourseEntity> findBySchools_IdAndIsActiveTrue(Long schoolId);

    Page<CourseEntity> findByIsActiveTrue(Pageable pageable);

    Page<CourseEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM CourseEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY c.createdAt DESC")
    Page<CourseEntity> searchByName(@Param("query") String query, Pageable pageable);

    List<CourseEntity> findByForLaggingStudentsTrueAndIsActiveTrue();

    List<CourseEntity> findByForLaggingStudentsTrueAndIsActiveTrueAndSchools_IdIn(List<Long> schoolIds);
}
