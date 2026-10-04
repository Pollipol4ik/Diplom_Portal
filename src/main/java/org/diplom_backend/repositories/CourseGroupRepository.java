package org.diplom_backend.repositories;

import org.diplom_backend.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {

    List<CourseGroup> findByCourse_Id(Long courseId);

    List<CourseGroup> findByCourse_IdAndSchool_Id(Long courseId, Long schoolId);
}
