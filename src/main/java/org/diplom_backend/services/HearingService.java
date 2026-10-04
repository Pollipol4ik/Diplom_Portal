package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.*;
import org.diplom_backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HearingService {

    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final HearingReviewRepository hearingReviewRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final CourseGroupMemberRepository courseGroupMemberRepository;
    private final AccountRepository accountRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public HearingSubmission submitHearing(Long lessonId, Long groupId, MultipartFile file, Account account) {
        CourseLessonEntity lesson = courseLessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityModelNotFoundException("Урок", "id", lessonId.toString()));

        if (lesson.getCategory() != LessonCategory.HEARING) {
            throw new IllegalArgumentException("Это не слушание");
        }

        if (lesson.getHearingStage() != HearingStage.TOPIC_APPROVAL
                && !Boolean.TRUE.equals(lesson.getHearingOpenForStudents())) {
            throw new IllegalStateException("Этап слушания ещё не открыт модератором");
        }

        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));

        verifyGroupOwner(account, group);

        Optional<HearingSubmission> existing = hearingSubmissionRepository.findByLesson_IdAndGroup_Id(lessonId, groupId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Работа по этому слушанию уже отправлена. Используйте повторную отправку.");
        }

        FileEntity fileEntity = null;
        if (file != null && !file.isEmpty()) {
            fileEntity = fileService.store(file);
            fileEntity = fileRepository.save(fileEntity);
        }

        LocalDateTime now = LocalDateTime.now();
        HearingSubmission submission = HearingSubmission.builder()
                .lesson(lesson)
                .group(group)
                .file(fileEntity)
                .status(ProjectStatus.ON_REVIEW)
                .currentVersion(1)
                .submittedAt(now)
                .updatedAt(now)
                .build();

        HearingSubmission saved = hearingSubmissionRepository.save(submission);
        var members = courseGroupMemberRepository.findByGroup_Id(group.getId());
        var owner = members.stream().filter(m -> Boolean.TRUE.equals(m.getIsOwner())).findFirst().orElse(null);
        // Перезагружаем аккаунт через репозиторий, чтобы lazy-поля school/schoolClass были доступны
        Account ownerAcc = owner != null
                ? accountRepository.findById(owner.getAccount().getId()).orElse(owner.getAccount())
                : null;
        String fio = ownerAcc != null
                ? ((ownerAcc.getLastName() != null ? ownerAcc.getLastName() + " " : "")
                + (ownerAcc.getFirstName() != null ? ownerAcc.getFirstName() : "")).trim()
                : null;
        telegramNotificationService.notifyModeratorsAboutHearingSubmission(
                lesson.getCourse().getId(),
                lesson.getCourse().getName(),
                lesson.getTitle(),
                group.getTitle(),
                (fio != null && !fio.isBlank()) ? fio : (ownerAcc != null ? "@" + ownerAcc.getNickname() : null),
                ownerAcc != null && ownerAcc.getSchool() != null ? ownerAcc.getSchool().getName() : null,
                ownerAcc != null && ownerAcc.getSchoolClass() != null ? ownerAcc.getSchoolClass().getName() : null
        );
        return saved;
    }

    @Transactional
    public HearingSubmission resubmitHearing(Long lessonId, Long groupId, MultipartFile file, Account account) {
        HearingSubmission submission = hearingSubmissionRepository.findByLesson_IdAndGroup_Id(lessonId, groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Отправка", "lessonId/groupId",
                        lessonId + "/" + groupId));

        CourseLessonEntity lesson = submission.getLesson();
        if (lesson.getCategory() != LessonCategory.HEARING) {
            throw new IllegalArgumentException("Это не слушание");
        }
        if (lesson.getHearingStage() != HearingStage.TOPIC_APPROVAL
                && !Boolean.TRUE.equals(lesson.getHearingOpenForStudents())) {
            throw new IllegalStateException("Этап слушания ещё не открыт модератором");
        }

        verifyGroupOwner(account, submission.getGroup());

        if (submission.getStatus() == ProjectStatus.ACCEPTED) {
            throw new IllegalStateException("Работа уже принята, повторная отправка невозможна");
        }

        if (file != null && !file.isEmpty()) {
            if (submission.getFile() != null) {
                fileService.delete(submission.getFile().getFileNameInDirectory());
            }
            FileEntity newFile = fileService.store(file);
            newFile = fileRepository.save(newFile);
            submission.setFile(newFile);
        }

        submission.setCurrentVersion(submission.getCurrentVersion() + 1);
        submission.setStatus(ProjectStatus.ON_REVIEW);
        submission.setUpdatedAt(LocalDateTime.now());

        HearingSubmission saved = hearingSubmissionRepository.save(submission);
        var members = courseGroupMemberRepository.findByGroup_Id(submission.getGroup().getId());
        var owner = members.stream().filter(m -> Boolean.TRUE.equals(m.getIsOwner())).findFirst().orElse(null);
        // Перезагружаем аккаунт через репозиторий, чтобы lazy-поля school/schoolClass были доступны
        Account ownerAcc = owner != null
                ? accountRepository.findById(owner.getAccount().getId()).orElse(owner.getAccount())
                : null;
        String fio = ownerAcc != null
                ? ((ownerAcc.getLastName() != null ? ownerAcc.getLastName() + " " : "")
                + (ownerAcc.getFirstName() != null ? ownerAcc.getFirstName() : "")).trim()
                : null;
        telegramNotificationService.notifyModeratorsAboutHearingSubmission(
                lesson.getCourse().getId(),
                lesson.getCourse().getName(),
                lesson.getTitle(),
                submission.getGroup().getTitle(),
                (fio != null && !fio.isBlank()) ? fio : (ownerAcc != null ? "@" + ownerAcc.getNickname() : null),
                ownerAcc != null && ownerAcc.getSchool() != null ? ownerAcc.getSchool().getName() : null,
                ownerAcc != null && ownerAcc.getSchoolClass() != null ? ownerAcc.getSchoolClass().getName() : null
        );
        return saved;
    }

    @Transactional
    public HearingReview reviewHearing(Long submissionId, String comment, Integer grade,
                                       ProjectStatus newStatus, Account moderator) {
        HearingSubmission submission = hearingSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException("Отправка", "id", submissionId.toString()));
        // ВАЖНО: истории не должны затираться. Всегда создаём новую запись рецензии/комментария.
        // Если работа уже принята — запрещаем переоценивать/менять статус: оставляем только комментарии.
        if (submission.getStatus() == ProjectStatus.ACCEPTED) {
            newStatus = ProjectStatus.ACCEPTED;
            grade = null;
        }
        if (newStatus != null) submission.setStatus(newStatus);
        submission.setUpdatedAt(LocalDateTime.now());
        hearingSubmissionRepository.save(submission);

        HearingReview review = HearingReview.builder()
                .submission(submission)
                .moderator(moderator)
                .comment(comment)
                .grade(grade)
                .submissionVersion(submission.getCurrentVersion() != null ? submission.getCurrentVersion() : 1)
                .reviewedAt(LocalDateTime.now())
                .build();

        HearingReview saved = hearingReviewRepository.save(review);

        // Уведомление всем участникам группы (и ученикам) о результате проверки/комментарии
        List<Long> memberIds = courseGroupMemberRepository.findByGroup_Id(submission.getGroup().getId()).stream()
                .map(m -> m.getAccount().getId())
                .toList();
        boolean isCommentOnly = (grade == null) && (comment != null && !comment.isBlank())
                && (newStatus == null || newStatus == ProjectStatus.ON_REVIEW || newStatus == ProjectStatus.ACCEPTED);
        if (isCommentOnly) {
            telegramNotificationService.notifyGroupMembersAboutHearingComment(
                    memberIds,
                    submission.getGroup().getTitle(),
                    submission.getLesson().getTitle(),
                    comment
            );
        } else {
            telegramNotificationService.notifyGroupMembersAboutReview(
                    memberIds,
                    submission.getGroup().getTitle(),
                    submission.getStatus().getDescription(),
                    comment,
                    grade
            );
        }
        return saved;
    }

    /**
     * Комментарий ученика по слушанию (переписка с модератором). Оценку и статус не меняет.
     */
    @Transactional
    public HearingReview addStudentComment(Long submissionId, String comment, Account student) {
        HearingSubmission submission = hearingSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException("Отправка", "id", submissionId.toString()));
        CourseGroup group = submission.getGroup();
        courseGroupMemberRepository.findByAccountAndCourse(student.getId(), group.getCourse().getId())
                .filter(m -> m.getGroup().getId().equals(group.getId()))
                .orElseThrow(() -> new NotEnoughRightsException("Вы не состоите в этой группе"));

        String trimmed = comment != null ? comment.trim() : "";
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым");
        }

        HearingReview row = HearingReview.builder()
                .submission(submission)
                .moderator(student)
                .comment(trimmed)
                .grade(null)
                .submissionVersion(submission.getCurrentVersion() != null ? submission.getCurrentVersion() : 1)
                .reviewedAt(LocalDateTime.now())
                .build();
        HearingReview saved = hearingReviewRepository.save(row);
        CourseLessonEntity lesson = submission.getLesson();
        telegramNotificationService.notifyModeratorsAboutHearingComment(
                lesson.getCourse().getId(),
                lesson.getCourse().getName(),
                lesson.getTitle(),
                submission.getGroup().getTitle(),
                student.getNickname(),
                trimmed
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public List<HearingSubmission> getSubmissionsByLesson(Long lessonId) {
        return hearingSubmissionRepository.findByLesson_IdWithReviews(lessonId);
    }

    @Transactional(readOnly = true)
    public List<HearingSubmission> getSubmissionsByGroup(Long groupId) {
        return hearingSubmissionRepository.findByGroup_Id(groupId);
    }

    @Transactional(readOnly = true)
    public HearingSubmission getSubmissionByLessonAndGroup(Long lessonId, Long groupId) {
        return hearingSubmissionRepository.findByLesson_IdAndGroup_IdWithReviews(lessonId, groupId).orElse(null);
    }

    @Transactional(readOnly = true)
    public HearingSubmission getSubmissionByIdWithReviews(Long submissionId) {
        return hearingSubmissionRepository.findByIdWithReviews(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException("Отправка", "id", submissionId.toString()));
    }

    @Transactional(readOnly = true)
    public List<HearingReview> getReviewsBySubmission(Long submissionId) {
        return hearingReviewRepository.findBySubmission_Id(submissionId);
    }

    private void verifyGroupOwner(Account account, CourseGroup group) {
        CourseGroupMember member = courseGroupMemberRepository
                .findByAccountAndCourse(account.getId(), group.getCourse().getId())
                .orElseThrow(() -> new NotEnoughRightsException("Вы не являетесь участником этой группы"));

        if (!member.getGroup().getId().equals(group.getId())) {
            throw new NotEnoughRightsException("Вы не являетесь участником этой группы");
        }

        if (!member.getIsOwner()) {
            throw new NotEnoughRightsException("Только владелец группы может отправлять работы");
        }
    }
}