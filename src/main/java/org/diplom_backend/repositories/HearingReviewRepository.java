package org.diplom_backend.repositories;

import org.diplom_backend.model.HearingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HearingReviewRepository extends JpaRepository<HearingReview, Long> {
    List<HearingReview> findBySubmission_Id(Long submissionId);

    @Query("SELECT r FROM HearingReview r WHERE r.submission.group.id = :groupId")
    List<HearingReview> findAllByGroupId(@Param("groupId") Long groupId);
}
