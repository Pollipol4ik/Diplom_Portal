package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.HearingReviewResponseDto;
import org.diplom_backend.dto.responses.HearingSubmissionResponseDto;
import org.diplom_backend.model.HearingReview;
import org.diplom_backend.model.HearingSubmission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Comparator;

@Component
public class HearingSubmissionMapper {

    public HearingSubmissionResponseDto toResponseDto(HearingSubmission submission) {
        final int currentVersion = submission.getCurrentVersion() != null ? submission.getCurrentVersion() : 1;
        List<HearingReviewResponseDto> reviews = submission.getReviews() == null
                ? List.of()
                : submission.getReviews().stream()
                // При смене темы увеличивается currentVersion — показываем только комментарии текущей версии.
                .filter(r -> {
                    Integer v = r.getSubmissionVersion();
                    return v == null ? currentVersion == 1 : v == currentVersion;
                })
                .sorted(Comparator.comparing(HearingReview::getReviewedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toReviewDto)
                .toList();

        return new HearingSubmissionResponseDto(
                submission.getId(),
                submission.getLesson().getId(),
                submission.getGroup().getId(),
                submission.getFile() != null ? submission.getFile().getInitialFileName() : null,
                submission.getFile() != null ? submission.getFile().getFileNameInDirectory() : null,
                submission.getStatus(),
                submission.getCurrentVersion(),
                submission.getSubmittedAt(),
                submission.getUpdatedAt(),
                reviews
        );
    }

    public HearingReviewResponseDto toReviewDto(HearingReview review) {
        String moderatorName = review.getModerator().getLastName() != null
                ? review.getModerator().getLastName() + " " + review.getModerator().getFirstName()
                : review.getModerator().getNickname();

        return new HearingReviewResponseDto(
                review.getId(),
                review.getModerator().getId(),
                moderatorName,
                review.getComment(),
                review.getGrade(),
                review.getReviewedAt()
        );
    }
}
