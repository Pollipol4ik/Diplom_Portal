package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.RatingWorkSnippetDto;
import org.diplom_backend.dto.responses.StudentRatingResponseDto;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.HearingReview;
import org.diplom_backend.model.HearingSubmission;
import org.diplom_backend.model.LessonCategory;
import org.diplom_backend.model.LessonSubmissionEntity;
import org.diplom_backend.model.Role;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseGroupMemberRepository;
import org.diplom_backend.repositories.CourseModeratorRepository;
import org.diplom_backend.repositories.HearingReviewRepository;
import org.diplom_backend.repositories.HearingSubmissionRepository;
import org.diplom_backend.repositories.LessonSubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentRatingService {

    private final AccountRepository accountRepository;
    private final HearingReviewRepository hearingReviewRepository;
    private final CourseGroupMemberRepository courseGroupMemberRepository;
    private final LessonSubmissionRepository lessonSubmissionRepository;
    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final CourseModeratorService courseModeratorService;
    private final CourseModeratorRepository courseModeratorRepository;
    private final StudentCourseEnrollmentService enrollmentService;

    @Transactional(readOnly = true)
    public Page<StudentRatingResponseDto> getRatings(Long schoolId, Long classId,
                                                     int page, int size,
                                                     Account moderator) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> students;

        if (schoolId != null) {
            courseModeratorService.verifyModeratorAccessToSchool(moderator, schoolId);
            if (classId != null) {
                students = accountRepository.findBySchoolClassId(classId, pageable);
            } else {
                students = accountRepository.findBySchoolId(schoolId, pageable);
            }
        } else {
            students = resolveStudentsPageAllSchools(classId, pageable, moderator);
        }

        List<StudentRatingResponseDto> ratings = students.getContent().stream()
                .filter(a -> a.getRole().getName() == Role.ROLE_USER)
                .map(this::buildRating)
                .collect(Collectors.toList());

        return new PageImpl<>(ratings, pageable, students.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<StudentRatingResponseDto> getLaggingStudents(Long schoolId,
                                                             int page, int size,
                                                             Account moderator) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> laggingPage;

        if (schoolId != null) {
            courseModeratorService.verifyModeratorAccessToSchool(moderator, schoolId);
            laggingPage = accountRepository.findLaggingStudentsBySchool(schoolId, pageable);
        } else {
            Role modRole = moderator.getRole().getName();
            if (modRole == Role.ROLE_ADMIN) {
                laggingPage = accountRepository.findAllLaggingStudents(pageable);
            } else {
                List<Long> schoolIds = moderatorAccessibleSchoolIds(moderator);
                if (schoolIds.isEmpty()) {
                    return Page.empty(pageable);
                }
                laggingPage = accountRepository.findLaggingStudentsBySchoolIds(schoolIds, pageable);
            }
        }

        List<StudentRatingResponseDto> lagging = laggingPage.getContent().stream()
                .filter(a -> a.getRole().getName() == Role.ROLE_USER)
                .map(this::buildRating)
                .collect(Collectors.toList());

        return new PageImpl<>(lagging, pageable, laggingPage.getTotalElements());
    }

    private Page<Account> resolveStudentsPageAllSchools(Long classId, Pageable pageable, Account moderator) {
        Role modRole = moderator.getRole().getName();
        if (modRole == Role.ROLE_ADMIN) {
            if (classId != null) {
                return accountRepository.findBySchoolClassId(classId, pageable);
            }
            return accountRepository.findAllStudentsPage(pageable);
        }
        List<Long> schoolIds = moderatorAccessibleSchoolIds(moderator);
        if (schoolIds.isEmpty()) {
            return Page.empty(pageable);
        }
        if (classId != null) {
            return accountRepository.findBySchoolClassIdAndSchool_IdIn(classId, schoolIds, pageable);
        }
        return accountRepository.findStudentsBySchoolIds(schoolIds, pageable);
    }

    private List<Long> moderatorAccessibleSchoolIds(Account moderator) {
        return courseModeratorService.getAccessibleSchoolIds(moderator);
    }

    private StudentRatingResponseDto buildRating(Account student) {
        Double projectRating = calculateProjectRating(student.getId());
        Double courseRating = calculateCourseRating(student.getId());
        Double combined = combinedRating(projectRating, courseRating);

        List<Long> enrolledCourseIds = courseGroupMemberRepository.findAllByAccount(student.getId())
                .stream()
                .map(m -> m.getGroup().getCourse().getId())
                .distinct()
                .collect(Collectors.toList());
        List<Long> forcedCourseIds = new ArrayList<>(enrollmentService.getForcedCourseIds(student.getId()));

        return new StudentRatingResponseDto(
                student.getId(),
                student.getNickname(),
                student.getFirstName(),
                student.getLastName(),
                student.getMiddleName(),
                student.getSchool() != null ? student.getSchool().getName() : null,
                student.getSchoolClass() != null ? student.getSchoolClass().getName() : null,
                projectRating,
                courseRating,
                combined,
                student.getIsLagging(),
                buildCourseRatingWorkSnippet(student.getId()),
                buildProjectRatingWorkSnippet(student.getId()),
                enrolledCourseIds,
                forcedCourseIds
        );
    }

    private RatingWorkSnippetDto buildCourseRatingWorkSnippet(Long accountId) {
        List<LessonSubmissionEntity> ranked = lessonSubmissionRepository.findAllByAccount_Id(accountId).stream()
                .filter(s -> s.getLesson() != null && s.getLesson().getCategory() != LessonCategory.HEARING)
                .filter(s -> s.getScore() != null)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getScore(), a.getScore());
                    if (cmp != 0) return cmp;
                    LocalDateTime ta = a.getSubmittedAt();
                    LocalDateTime tb = b.getSubmittedAt();
                    if (ta == null) return 1;
                    if (tb == null) return -1;
                    return tb.compareTo(ta);
                })
                .toList();
        if (ranked.isEmpty()) return null;
        LessonSubmissionEntity s = ranked.get(0);
        var f = s.getFile();
        return new RatingWorkSnippetDto(
                "LESSON",
                s.getLesson().getCourse().getId(),
                s.getLesson().getCourse().getName(),
                s.getLesson().getId(),
                s.getLesson().getTitle(),
                null,
                s.getTextContent(),
                f != null ? f.getInitialFileName() : null,
                f != null ? f.getFileNameInDirectory() : null,
                s.getScore(),
                null,
                s.getStatus() != null ? s.getStatus().name() : null
        );
    }

    private RatingWorkSnippetDto buildProjectRatingWorkSnippet(Long accountId) {
        record Cand(HearingSubmission hs, Integer grade) {}
        List<Cand> cands = new ArrayList<>();
        for (var membership : courseGroupMemberRepository.findAllByAccount(accountId)) {
            for (HearingSubmission hs : hearingSubmissionRepository.findByGroup_Id(membership.getGroup().getId())) {
                if (hs.getLesson() == null || hs.getLesson().getCategory() != LessonCategory.HEARING) continue;
                cands.add(new Cand(hs, maxModeratorGradeForCurrentVersion(hs)));
            }
        }
        if (cands.isEmpty()) return null;
        cands.sort((a, b) -> {
            int ga = a.grade != null ? a.grade : -1;
            int gb = b.grade != null ? b.grade : -1;
            if (gb != ga) return Integer.compare(gb, ga);
            LocalDateTime ta = a.hs.getSubmittedAt();
            LocalDateTime tb = b.hs.getSubmittedAt();
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });
        HearingSubmission hs = cands.get(0).hs;
        Integer repGrade = cands.get(0).grade;
        var f = hs.getFile();
        return new RatingWorkSnippetDto(
                "HEARING",
                hs.getLesson().getCourse().getId(),
                hs.getLesson().getCourse().getName(),
                hs.getLesson().getId(),
                hs.getLesson().getTitle(),
                hs.getGroup().getTitle(),
                null,
                f != null ? f.getInitialFileName() : null,
                f != null ? f.getFileNameInDirectory() : null,
                null,
                repGrade,
                hs.getStatus() != null ? hs.getStatus().name() : null
        );
    }

    private Integer maxModeratorGradeForCurrentVersion(HearingSubmission hs) {
        int version = hs.getCurrentVersion() != null ? hs.getCurrentVersion() : 1;
        Integer max = null;
        for (HearingReview r : hearingReviewRepository.findBySubmission_Id(hs.getId())) {
            if (r.getGrade() == null) continue;
            Integer rv = r.getSubmissionVersion();
            if (rv != null && rv != version) continue;
            max = max == null ? r.getGrade() : Math.max(max, r.getGrade());
        }
        return max;
    }

    // ==================== ИСПРАВЛЕННЫЕ МЕТОДЫ ====================

    /**
     * Сумма всех баллов по слушаниям (проектам).
     * Берётся максимум по каждой отправке (текущей версии).
     */
    private Double calculateProjectRating(Long accountId) {
        var memberships = courseGroupMemberRepository.findAllByAccount(accountId);
        if (memberships.isEmpty()) return null;

        double sum = 0;
        int count = 0;
        for (var membership : memberships) {
            List<HearingReview> reviews = hearingReviewRepository.findAllByGroupId(membership.getGroup().getId());
            java.util.Map<Long, Integer> maxBySubmission = new java.util.HashMap<>();
            for (HearingReview review : reviews) {
                if (review.getGrade() == null || review.getSubmission() == null) continue;
                Integer curV = review.getSubmission().getCurrentVersion() != null ? review.getSubmission().getCurrentVersion() : 1;
                Integer rv = review.getSubmissionVersion();
                if (rv != null && !rv.equals(curV)) continue;
                Long sid = review.getSubmission().getId();
                Integer prev = maxBySubmission.get(sid);
                maxBySubmission.put(sid, prev == null ? review.getGrade() : Math.max(prev, review.getGrade()));
            }
            for (Integer v : maxBySubmission.values()) {
                sum += v;
                count++;
            }
        }
        return count > 0 ? Math.round(sum * 100.0) / 100.0 : null;
    }

    /**
     * Сумма всех баллов по урокам (не слушания).
     */
    private Double calculateCourseRating(Long accountId) {
        var submissions = lessonSubmissionRepository.findAllByAccount_Id(accountId).stream()
                .filter(s -> s.getScore() != null)
                .toList();
        if (submissions.isEmpty()) return null;

        double sum = submissions.stream().mapToInt(s -> s.getScore()).sum();
        return Math.round(sum * 100.0) / 100.0;
    }

    /**
     * Общий балл = сумма projectRating + courseRating.
     * Если один из рейтингов отсутствует — возвращается второй.
     */
    private Double combinedRating(Double projectRating, Double courseRating) {
        if (projectRating == null && courseRating == null) return null;
        double p = projectRating != null ? projectRating : 0.0;
        double c = courseRating != null ? courseRating : 0.0;
        return Math.round((p + c) * 100.0) / 100.0;
    }
}