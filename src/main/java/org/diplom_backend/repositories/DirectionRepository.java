package org.diplom_backend.repositories;


import org.diplom_backend.model.DirectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с курсом
 */
@Repository
public interface DirectionRepository extends JpaRepository<DirectionEntity, Long> {
}
