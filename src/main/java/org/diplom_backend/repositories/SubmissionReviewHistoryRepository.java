package org.diplom_backend.repositories;

import org.diplom_backend.model.SubmissionReviewHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionReviewHistoryRepository extends JpaRepository<SubmissionReviewHistoryEntity, Long> {

    List<SubmissionReviewHistoryEntity> findBySubmission_IdOrderByCreatedAtAsc(Long submissionId);
}
