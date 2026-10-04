package org.diplom_backend.repositories;


import org.diplom_backend.model.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findByParentAndPublication_Id(CommentEntity parent, Long publication_id, Pageable pageable);

    Page<CommentEntity> findByParent_Id(Long comment_id, Pageable pageable);
}
