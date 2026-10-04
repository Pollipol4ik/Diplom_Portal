package org.diplom_backend.repositories;

import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectTopicRepository extends JpaRepository<SubjectTopicEntity, Long> {

    boolean existsBySubjectAndName(SubjectEntity subject, String name);

    Page<SubjectTopicEntity> findBySubject_Id(Long subjectId, Pageable pageable);

    @Query("SELECT st FROM SubjectTopicEntity st " +
            "LEFT JOIN FETCH st.subject s " +
            "LEFT JOIN FETCH s.direction " +
            "WHERE st.id = :id")
    Optional<SubjectTopicEntity> findByIdWithSubjectAndCourse(@Param("id") Long id);
}