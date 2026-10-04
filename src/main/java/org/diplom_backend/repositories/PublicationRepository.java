package org.diplom_backend.repositories;


import org.diplom_backend.model.PublicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Long> {
    Page<PublicationEntity> findBySubjectTopics_Id(Long id, Pageable pageable);
}