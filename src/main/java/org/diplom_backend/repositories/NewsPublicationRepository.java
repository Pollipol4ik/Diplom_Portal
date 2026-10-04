package org.diplom_backend.repositories;

import org.diplom_backend.model.NewsPublicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsPublicationRepository extends JpaRepository<NewsPublicationEntity, Long> {
}
