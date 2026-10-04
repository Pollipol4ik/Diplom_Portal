package org.diplom_backend.repositories;

import org.diplom_backend.model.SubjectModerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectModeratorRepository extends JpaRepository<SubjectModerator, Long> {

    boolean existsByAccount_IdAndSubject_Id(Long accountId, Long subjectId);

    List<SubjectModerator> findAllBySubject_Id(Long subjectId);

    void deleteByAccount_IdAndSubject_Id(Long accountId, Long subjectId);

    List<SubjectModerator> findAllByAccount_Id(Long accountId);
}