package org.diplom_backend.repositories;

import org.diplom_backend.model.SubjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    boolean existsSubjectByNameAndDirection_Id(String name, Long directionId);

    Page<SubjectEntity> findByDirection_Id(Long directionId, Pageable pageable);
}