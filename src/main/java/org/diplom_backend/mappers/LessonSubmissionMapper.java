package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.CriterionGradeResponseDto;
import org.diplom_backend.dto.responses.LessonSubmissionResponseDto;
import org.diplom_backend.model.LessonSubmissionEntity;
import org.diplom_backend.model.SubmissionCriterionGrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonSubmissionMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "lessonTitle", source = "lesson.title")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountNickname", source = "account.nickname")
    @Mapping(target = "fileName", source = "file.initialFileName")
    @Mapping(target = "fileNameInDirectory", source = "file.fileNameInDirectory")
    @Mapping(target = "maxScore", source = "lesson.maxScore")
    @Mapping(target = "criterionGrades", ignore = true)
    LessonSubmissionResponseDto toResponseDto(LessonSubmissionEntity submission);

    default LessonSubmissionResponseDto toResponseDto(LessonSubmissionEntity submission,
                                                       List<SubmissionCriterionGrade> grades) {
        LessonSubmissionResponseDto dto = toResponseDto(submission);
        List<CriterionGradeResponseDto> gradeDtos = grades == null ? List.of() :
                grades.stream().map(g -> new CriterionGradeResponseDto(
                        g.getId(),
                        g.getCriterion().getId(),
                        g.getCriterion().getName(),
                        g.getCriterion().getMaxPoints(),
                        g.getPoints()
                )).toList();

        return new LessonSubmissionResponseDto(
                dto.id(), dto.lessonId(), dto.lessonTitle(),
                dto.accountId(), dto.accountNickname(),
                dto.textContent(), dto.fileName(), dto.fileNameInDirectory(),
                dto.status(), dto.reviewerComment(), dto.score(), dto.maxScore(),
                dto.submittedAt(), dto.updatedAt(), gradeDtos
        );
    }

    default LessonSubmissionResponseDto toResponseDto(LessonSubmissionEntity submission,
                                                     Integer maxScore,
                                                     List<SubmissionCriterionGrade> grades) {
        LessonSubmissionResponseDto dto = toResponseDto(submission, grades);
        return new LessonSubmissionResponseDto(
                dto.id(), dto.lessonId(), dto.lessonTitle(),
                dto.accountId(), dto.accountNickname(),
                dto.textContent(), dto.fileName(), dto.fileNameInDirectory(),
                dto.status(), dto.reviewerComment(), dto.score(), maxScore,
                dto.submittedAt(), dto.updatedAt(), dto.criterionGrades()
        );
    }
}
