package org.diplom_backend.repositories;

import org.diplom_backend.model.SubmissionCriterionGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionCriterionGradeRepository extends JpaRepository<SubmissionCriterionGrade, Long> {

    List<SubmissionCriterionGrade> findBySubmission_Id(Long submissionId);

    void deleteAllBySubmission_Id(Long submissionId);
}
