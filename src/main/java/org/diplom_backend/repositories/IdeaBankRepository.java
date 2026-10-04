package org.diplom_backend.repositories;

import org.diplom_backend.model.IdeaBankEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaBankRepository extends JpaRepository<IdeaBankEntry, Long> {

    @Query("""
            SELECT i
            FROM IdeaBankEntry i
            WHERE (:query IS NULL OR :query = '' OR
                   LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minScore IS NULL OR i.score >= :minScore)
              AND (:maxScore IS NULL OR i.score <= :maxScore)
              AND (:courseId IS NULL OR i.course.id = :courseId)
            """)
    Page<IdeaBankEntry> search(@Param("query") String query,
                               @Param("minScore") Integer minScore,
                               @Param("maxScore") Integer maxScore,
                               @Param("courseId") Long courseId,
                               Pageable pageable);
}