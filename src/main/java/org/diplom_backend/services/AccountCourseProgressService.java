package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.AccountCourseProgressResponseDto;
import org.diplom_backend.model.*;
import org.diplom_backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountCourseProgressService {

    private final LessonSubmissionRepository submissionRepository;
    private final CourseModeratorRepository courseModeratorRepository;
    private final CourseRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseGroupMemberRepository groupMemberRepository;
    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final GradingCriterionRepository gradingCriterionRepository;
    private final AccountRepository accountRepository;

    private int maxScoreForLesson(CourseLessonEntity l) {
        List<GradingCriterion> criteria = gradingCriterionRepository.findByLesson_IdOrderByOrderNumberAsc(l.getId());
        if (criteria != null && !criteria.isEmpty()) {
            return criteria.stream().mapToInt(GradingCriterion::getMaxPoints).sum();
        }
        return l.getMaxScore() != null ? l.getMaxScore() : 0;
    }

    @Transactional(readOnly = true)
    public List<AccountCourseProgressResponseDto> getCourseProgress(Account account) {
        RoleEntity roleEntity = account.getRole();
        if (roleEntity != null && roleEntity.getName() == Role.ROLE_MODERATOR) {
            return getModeratorProgress(account);
        }
        return getStudentProgress(account);
    }

    private List<AccountCourseProgressResponseDto> getStudentProgress(Account account) {
        List<LessonSubmissionEntity> allSubs = submissionRepository.findAllByAccount_Id(account.getId());

        Map<Long, List<LessonSubmissionEntity>> subsByCourse = allSubs.stream()
                .collect(Collectors.groupingBy(s -> s.getLesson().getCourse().getId()));

        List<CourseGroupMember> memberships = groupMemberRepository.findAllByAccount(account.getId());

        Set<Long> courseIds = new HashSet<>(subsByCourse.keySet());
        memberships.forEach(m -> courseIds.add(m.getGroup().getCourse().getId()));

        // Вводные курсы отображаются в прогрессе без требования группы —
        // ученик видит их всегда, если его школа привязана к курсу
        Account fresh = accountRepository.findById(account.getId()).orElse(null);
        if (fresh != null && fresh.getSchool() != null) {
            courseRepository.findBySchools_IdAndIsActiveTrue(fresh.getSchool().getId())
                    .stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsIntroduction()))
                    .forEach(c -> courseIds.add(c.getId()));
        }

        List<AccountCourseProgressResponseDto> results = new ArrayList<>();
        for (Long courseId : courseIds) {
            CourseEntity course = courseRepository.findById(courseId).orElse(null);
            if (course == null) continue;

            List<CourseLessonEntity> lessons = lessonRepository.findByCourse_IdOrderByOrderNumberAsc(courseId);
            List<CourseLessonEntity> regularLessons = lessons.stream()
                    .filter(l -> l.getCategory() == LessonCategory.LESSON)
                    .toList();
            List<CourseLessonEntity> hearingLessons = lessons.stream()
                    .filter(l -> l.getCategory() == LessonCategory.HEARING)
                    .toList();

            List<LessonSubmissionEntity> courseSubs = subsByCourse.getOrDefault(courseId, List.of());
            int submitted = courseSubs.size();
            int accepted = (int) courseSubs.stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                    .count();
            double avgScore = courseSubs.stream()
                    .filter(s -> s.getScore() != null)
                    .mapToInt(LessonSubmissionEntity::getScore)
                    .average()
                    .orElse(0);
            int maxRegular = regularLessons.stream().mapToInt(this::maxScoreForLesson).sum();
            int maxHearing = hearingLessons.stream().mapToInt(this::maxScoreForLesson).sum();

            List<AccountCourseProgressResponseDto.HearingStageStatus> hearingStatuses = new ArrayList<>();

            CourseGroupMember membership = memberships.stream()
                    .filter(m -> m.getGroup().getCourse().getId().equals(courseId))
                    .findFirst().orElse(null);

            if (membership != null) {
                for (CourseLessonEntity hl : hearingLessons) {
                    Optional<HearingSubmission> hs = hearingSubmissionRepository
                            .findByLesson_IdAndGroup_Id(hl.getId(), membership.getGroup().getId());
                    hearingStatuses.add(AccountCourseProgressResponseDto.HearingStageStatus.builder()
                            .stage(hl.getHearingStage() != null ? hl.getHearingStage().name() : "UNKNOWN")
                            .status(hs.map(h -> h.getStatus().name()).orElse("NOT_SUBMITTED"))
                            .build());
                }
            }

            results.add(AccountCourseProgressResponseDto.builder()
                    .courseId(courseId)
                    .courseName(course.getName())
                    .totalLessons(regularLessons.size())
                    .submittedLessons(submitted)
                    .acceptedLessons(accepted)
                    .averageScore(Math.round(avgScore * 10.0) / 10.0)
                    .maxPossibleScore(maxRegular + maxHearing)
                    .hearingStatuses(hearingStatuses)
                    .build());
        }
        return results;
    }

    private List<AccountCourseProgressResponseDto> getModeratorProgress(Account account) {
        List<CourseModerator> assignments = courseModeratorRepository.findAllByAccount_Id(account.getId());

        List<AccountCourseProgressResponseDto> results = new ArrayList<>();
        for (CourseModerator cm : assignments) {
            CourseEntity course = cm.getCourse();
            List<CourseLessonEntity> lessons = lessonRepository.findByCourse_IdOrderByOrderNumberAsc(course.getId());
            int lessonCount = (int) lessons.stream().filter(l -> l.getCategory() == LessonCategory.LESSON).count();

            results.add(AccountCourseProgressResponseDto.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .totalLessons(lessonCount)
                    .submittedLessons(0)
                    .acceptedLessons(0)
                    .averageScore(0)
                    .maxPossibleScore(0)
                    .hearingStatuses(List.of())
                    .build());
        }
        return results;
    }
}