package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.CourseGroup;
import org.diplom_backend.model.CourseGroupMember;
import org.diplom_backend.model.HearingReview;
import org.diplom_backend.model.HearingStage;
import org.diplom_backend.model.HearingSubmission;
import org.diplom_backend.model.LessonCategory;
import org.diplom_backend.model.ProjectStatus;
import org.diplom_backend.model.School;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseGroupMemberRepository;
import org.diplom_backend.repositories.CourseGroupRepository;
import org.diplom_backend.repositories.CourseLessonRepository;
import org.diplom_backend.repositories.CourseRepository;
import org.diplom_backend.repositories.HearingReviewRepository;
import org.diplom_backend.repositories.HearingSubmissionRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseGroupService {

    private final CourseGroupRepository courseGroupRepository;
    private final CourseGroupMemberRepository courseGroupMemberRepository;
    private final CourseRepository courseRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final AccountRepository accountRepository;
    private final SchoolRepository schoolRepository;
    private final IdeaBankService ideaBankService;
    private final HearingReviewRepository hearingReviewRepository;
    private final HearingSubmissionRepository hearingSubmissionRepo;
    private final CourseLessonRepository courseLessonRepo;
    private final StudentCourseEnrollmentService enrollmentService;

    /**
     * @param clearLaggingForNewMembers если false — не снимать флаг отстающего (автоподключение к курсу поддержки)
     */
    @Transactional
    public CourseGroup createGroup(Long courseId, String title, String description,
                                   Long schoolId, List<Long> studentIds,
                                   Account moderator, boolean clearLaggingForNewMembers) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));

        if (studentIds != null) {
            for (Long sid : studentIds) {
                courseGroupMemberRepository.findByAccountAndCourse(sid, courseId).ifPresent(existing -> {
                    Account a = existing.getAccount();
                    throw new IllegalStateException(
                            "Ученик " + (a.getLastName() != null ? a.getLastName() + " " : "")
                                    + (a.getFirstName() != null ? a.getFirstName() : a.getNickname())
                                    + " уже состоит в группе «" + existing.getGroup().getTitle() + "»");
                });
            }
        }

        CourseGroup group = CourseGroup.builder()
                .course(course)
                .title(title)
                .description(description)
                .school(school)
                .build();
        group = courseGroupRepository.save(group);

        if (studentIds != null && !studentIds.isEmpty()) {
            addMembersToGroup(group, studentIds);
        }

        autoSubmitTopicApproval(course, group, title);

        if (clearLaggingForNewMembers && studentIds != null && !studentIds.isEmpty()) {
            for (Long sid : studentIds) {
                Account studentAcc = accountRepository.findById(sid).orElse(null);
                if (studentAcc != null && Boolean.TRUE.equals(studentAcc.getIsLagging())) {
                    studentAcc.setIsLagging(false);
                    accountRepository.save(studentAcc);
                }
            }
        }

        return group;
    }

    @Transactional
    public CourseGroup reassignGroup(Long groupId, String title, String description,
                                     List<Long> studentIds, Account moderator) {
        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));

        if (title != null && !title.isBlank() && !title.equals(group.getTitle())) {
            int v = getTopicApprovalSubmission(group).map(s -> s.getCurrentVersion() != null ? s.getCurrentVersion() : 1).orElse(1);
            String commentsFromReviews = collectHearingComments(group, v);
            ideaBankService.archiveFromProject(
                    group.getTitle(),
                    group.getDescription(),
                    commentsFromReviews,
                    null, groupId, moderator);
            group.setTitle(title);
        }
        if (description != null) {
            group.setDescription(description);
        }

        if (studentIds != null) {
            Long courseId = group.getCourse().getId();
            for (Long sid : studentIds) {
                courseGroupMemberRepository.findByAccountAndCourse(sid, courseId).ifPresent(existing -> {
                    if (!existing.getGroup().getId().equals(groupId)) {
                        Account a = existing.getAccount();
                        throw new IllegalStateException(
                                "Ученик " + (a.getLastName() != null ? a.getLastName() + " " : "")
                                        + (a.getFirstName() != null ? a.getFirstName() : a.getNickname())
                                        + " уже состоит в группе «" + existing.getGroup().getTitle() + "»");
                    }
                });
            }
            group.getMembers().clear();
            courseGroupRepository.save(group);
            addMembersToGroup(group, studentIds);
        }
        for (Long sid : studentIds) {
            Account studentAcc = accountRepository.findById(sid).orElse(null);
            if (studentAcc != null && Boolean.TRUE.equals(studentAcc.getIsLagging())) {
                studentAcc.setIsLagging(false);
                accountRepository.save(studentAcc);
            }
        }

        return courseGroupRepository.save(group);
    }

    /**
     * Ученик меняет название и описание темы своей группы (без изменения состава).
     */
    @Transactional
    public CourseGroup updateGroupTopicAsStudent(Long courseId, Long groupId, String title, String description,
                                                 Account student) {
        if (title == null && description == null) {
            throw new IllegalArgumentException("Укажите название или описание");
        }

        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));
        if (!group.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Группа не относится к этому курсу");
        }

        CourseGroupMember member = courseGroupMemberRepository.findByAccountAndCourse(student.getId(), courseId)
                .orElseThrow(() -> new NotEnoughRightsException("Вы не состоите в группе этого курса"));
        if (!member.getGroup().getId().equals(groupId)) {
            throw new NotEnoughRightsException("Это не ваша группа");
        }

        var topicSubOpt = getTopicApprovalSubmission(group);
        HearingSubmission topicSub = topicSubOpt.orElse(null);
        int oldVersion = topicSub != null && topicSub.getCurrentVersion() != null ? topicSub.getCurrentVersion() : 1;

        boolean titleChanged = title != null && !title.isBlank() && !title.equals(group.getTitle());
        if (titleChanged) {
            // Если тема уже была принята и оценка > 7 — архивируем старую тему в банк идей.
            Integer maxGrade = getTopicApprovalMaxGrade(group, oldVersion);
            if (maxGrade != null && maxGrade > 7) {
                String commentsFromReviews = collectHearingComments(group, oldVersion);
                ideaBankService.archiveFromProject(
                        group.getTitle(),
                        group.getDescription(),
                        commentsFromReviews,
                        maxGrade, groupId, student);
            }
            group.setTitle(title);
        }
        if (description != null) {
            group.setDescription(description);
        }

        // После принятия темы разрешаем обновить тему и возвращаем работу на проверку.
        if (titleChanged || description != null) {
            resetTopicApprovalSubmissionToReview(group);
        }

        return courseGroupRepository.save(group);
    }

    private void resetTopicApprovalSubmissionToReview(CourseGroup group) {
        Long courseId = group.getCourse().getId();
        var topicLessons = courseLessonRepo.findByCourse_IdAndCategoryOrderByOrderNumberAsc(
                courseId, org.diplom_backend.model.LessonCategory.HEARING);
        for (var lesson : topicLessons) {
            if (lesson.getHearingStage() != org.diplom_backend.model.HearingStage.TOPIC_APPROVAL) continue;
            hearingSubmissionRepo.findByLesson_IdAndGroup_Id(lesson.getId(), group.getId()).ifPresent(sub -> {
                sub.setStatus(ProjectStatus.ON_REVIEW);
                sub.setCurrentVersion(sub.getCurrentVersion() != null ? sub.getCurrentVersion() + 1 : 1);
                sub.setUpdatedAt(LocalDateTime.now());
                hearingSubmissionRepo.save(sub);
            });
        }
    }

    private java.util.Optional<HearingSubmission> getTopicApprovalSubmission(CourseGroup group) {
        Long courseId = group.getCourse().getId();
        var topicLessons = courseLessonRepo.findByCourse_IdAndCategoryOrderByOrderNumberAsc(
                courseId, org.diplom_backend.model.LessonCategory.HEARING);
        for (var lesson : topicLessons) {
            if (lesson.getHearingStage() != org.diplom_backend.model.HearingStage.TOPIC_APPROVAL) continue;
            return hearingSubmissionRepo.findByLesson_IdAndGroup_Id(lesson.getId(), group.getId());
        }
        return java.util.Optional.empty();
    }

    private Integer getTopicApprovalMaxGrade(CourseGroup group, int version) {
        Long courseId = group.getCourse().getId();
        var topicLessons = courseLessonRepo.findByCourse_IdAndCategoryOrderByOrderNumberAsc(
                courseId, org.diplom_backend.model.LessonCategory.HEARING);
        for (var lesson : topicLessons) {
            if (lesson.getHearingStage() != org.diplom_backend.model.HearingStage.TOPIC_APPROVAL) continue;
            var submission = hearingSubmissionRepo.findByLesson_IdAndGroup_Id(lesson.getId(), group.getId());
            if (submission.isEmpty()) continue;
            if (submission.get().getStatus() != ProjectStatus.ACCEPTED) continue;
            Integer max = null;
            var reviews = hearingReviewRepository.findBySubmission_Id(submission.get().getId());
            for (var r : reviews) {
                if (r.getGrade() == null) continue;
                if (r.getSubmissionVersion() != null && r.getSubmissionVersion() != version) continue;
                max = max == null ? r.getGrade() : Math.max(max, r.getGrade());
            }
            return max;
        }
        return null;
    }

    /**
     * Ученик создаёт свою группу в курсе (тему+описание) и автоматически становится владельцем.
     * Дополнительно можно указать одноклассников из того же класса.
     */
    @Transactional
    public CourseGroup createMyGroup(Long courseId, String title, String description,
                                     List<Long> classmateAccountIds, Account student) {
        if (student.getSchool() == null) {
            throw new IllegalStateException("Для выбора темы сначала укажите школу в профиле");
        }
        java.util.List<Long> mates = classmateAccountIds == null ? List.of() : classmateAccountIds;
        if (!mates.isEmpty() && student.getSchoolClass() == null) {
            throw new IllegalStateException("Укажите класс в профиле, чтобы добавить одноклассников в группу");
        }
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        // Если курс привязан к школам — ограничиваем выбор тем только ученикам из этих школ.
        if (course.getSchools() != null && !course.getSchools().isEmpty()) {
            boolean ok = course.getSchools().stream().anyMatch(s -> s.getId().equals(student.getSchool().getId()));
            if (!ok) {
                throw new NotEnoughRightsException("Вы не можете выбрать тему в этом курсе (курс другой школы)");
            }
        }

        // Получаем ID учеников, зачисленных на этот курс (через группу ИЛИ принудительно)
        Set<Long> enrolledOnCourse = new HashSet<>();

        // Через группы
        List<CourseGroupMember> existingMembers = courseGroupMemberRepository.findAllByCourseId(courseId);
        enrolledOnCourse.addAll(existingMembers.stream()
                .map(m -> m.getAccount().getId())
                .collect(Collectors.toSet()));

        // Через принудительное назначение
        enrolledOnCourse.addAll(enrollmentService.getStudentIdsForcedToCourse(courseId));

        java.util.LinkedHashSet<Long> memberIds = new java.util.LinkedHashSet<>();
        memberIds.add(student.getId());
        Long leaderClassId = student.getSchoolClass() != null ? student.getSchoolClass().getId() : null;

        for (Long cid : mates) {
            if (cid == null || cid.equals(student.getId())) {
                continue;
            }
            Account peer = accountRepository.findById(cid)
                    .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", String.valueOf(cid)));

            // ✅ Проверяем, что одноклассник зачислен на этот курс
            if (!enrolledOnCourse.contains(cid)) {
                throw new IllegalStateException("Ученик «" + peer.getNickname() + "» не зачислен на этот курс");
            }

            if (peer.getSchool() == null || !peer.getSchool().getId().equals(student.getSchool().getId())) {
                throw new IllegalStateException("Можно добавить только учеников из вашей школы");
            }
            if (leaderClassId == null || peer.getSchoolClass() == null
                    || !peer.getSchoolClass().getId().equals(leaderClassId)) {
                throw new IllegalStateException("Можно добавить только учеников из вашего класса");
            }
            courseGroupMemberRepository.findByAccountAndCourse(cid, courseId).ifPresent(existing -> {
                throw new IllegalStateException("Ученик «"
                        + (peer.getLastName() != null ? peer.getLastName() + " " : "")
                        + (peer.getFirstName() != null ? peer.getFirstName() : peer.getNickname())
                        + "» уже состоит в группе «" + existing.getGroup().getTitle() + "» этого курса");
            });
            memberIds.add(cid);
        }

        return createGroup(
                courseId,
                title,
                description,
                student.getSchool().getId(),
                new java.util.ArrayList<>(memberIds),
                student,
                true
        );
    }

    /**
     * Применить идею из банка идей к группе: обновить тему/описание и вернуть TOPIC_APPROVAL на проверку.
     * Используется модератором.
     */
    @Transactional
    public CourseGroup applyIdeaToGroup(Long groupId, String title, String description, Account moderator) {
        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));
        if (title != null && !title.isBlank()) {
            group.setTitle(title.trim());
        }
        if (description != null) {
            group.setDescription(description.trim());
        }
        resetTopicApprovalSubmissionToReview(group);
        return courseGroupRepository.save(group);
    }

    @Transactional
    public void appendIdeaHistoryToCurrentTopic(Long groupId, String ideaDescription, String ideaComments,
                                                Integer ideaScore, Account actor) {
        CourseGroup group = getGroupById(groupId);
        var subOpt = getTopicApprovalSubmission(group);
        if (subOpt.isEmpty()) return;
        HearingSubmission sub = subOpt.get();
        int v = sub.getCurrentVersion() != null ? sub.getCurrentVersion() : 1;

        StringBuilder sb = new StringBuilder();
        sb.append("[Банк идей]");
        if (ideaScore != null) sb.append(" баллы: ").append(ideaScore);
        sb.append("\n");
        if (ideaDescription != null && !ideaDescription.isBlank()) {
            sb.append("Описание: ").append(ideaDescription.trim()).append("\n");
        }
        if (ideaComments != null && !ideaComments.isBlank()) {
            sb.append("История:\n").append(ideaComments.trim());
        }
        String text = sb.toString().trim();
        if (text.isEmpty()) return;

        HearingReview row = HearingReview.builder()
                .submission(sub)
                .moderator(actor)
                .comment(text)
                .grade(null)
                .submissionVersion(v)
                .reviewedAt(LocalDateTime.now())
                .build();
        hearingReviewRepository.save(row);
    }

    @Transactional(readOnly = true)
    public List<CourseGroup> getGroupsByCourse(Long courseId) {
        return courseGroupRepository.findByCourse_Id(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseGroup> getGroupsByCourseAndSchool(Long courseId, Long schoolId) {
        return courseGroupRepository.findByCourse_IdAndSchool_Id(courseId, schoolId);
    }

    @Transactional(readOnly = true)
    public CourseGroup getGroupById(Long groupId) {
        return courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));
    }

    @Transactional(readOnly = true)
    public CourseGroupMember getMyGroupForCourse(Long accountId, Long courseId) {
        return courseGroupMemberRepository.findByAccountAndCourse(accountId, courseId).orElse(null);
    }

    /**
     * Назначить тему из банка идей ученику: создаёт группу в курсе, только если у ученика ещё нет группы.
     */
    @Transactional
    public CourseGroup createGroupForStudentFromIdea(Long courseId, Long studentId,
                                                     String title, String description,
                                                     Account moderator) {
        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", studentId.toString()));
        if (student.getSchool() == null) {
            throw new IllegalStateException("У ученика не указана школа");
        }
        courseGroupMemberRepository.findByAccountAndCourse(studentId, courseId).ifPresent(existing -> {
            throw new IllegalStateException("Нельзя назначить тему: ученик уже состоит в группе «"
                    + existing.getGroup().getTitle() + "»");
        });
        return createGroup(
                courseId,
                title,
                description,
                student.getSchool().getId(),
                List.of(studentId),
                moderator,
                true
        );
    }

    private void addMembersToGroup(CourseGroup group, List<Long> studentIds) {
        boolean first = true;
        for (Long studentId : studentIds) {
            Account student = accountRepository.findById(studentId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", studentId.toString()));

            CourseGroupMember member = CourseGroupMember.builder()
                    .group(group)
                    .account(student)
                    .isOwner(first)
                    .joinedAt(LocalDateTime.now())
                    .build();
            courseGroupMemberRepository.save(member);
            first = false;
        }
    }

    /**
     * Собирает все комментарии из hearing reviews по TOPIC_APPROVAL для этой группы.
     */
    private String collectHearingComments(CourseGroup group, int version) {
        Long courseId = group.getCourse().getId();
        var topicLessons = courseLessonRepo.findByCourse_IdAndCategoryOrderByOrderNumberAsc(
                courseId, org.diplom_backend.model.LessonCategory.HEARING);

        StringBuilder sb = new StringBuilder();
        for (var lesson : topicLessons) {
            if (lesson.getHearingStage() != org.diplom_backend.model.HearingStage.TOPIC_APPROVAL) continue;
            var submission = hearingSubmissionRepo.findByLesson_IdAndGroup_Id(lesson.getId(), group.getId());
            if (submission.isEmpty()) continue;
            var reviews = hearingReviewRepository.findBySubmission_Id(submission.get().getId());
            for (var review : reviews) {
                if (review.getSubmissionVersion() != null && review.getSubmissionVersion() != version) continue;
                if (review.getComment() != null && !review.getComment().isBlank()) {
                    String reviewerName = review.getModerator().getNickname();
                    sb.append("[").append(reviewerName).append("]");
                    if (review.getGrade() != null) {
                        sb.append(" оценка: ").append(review.getGrade());
                    }
                    sb.append(": ").append(review.getComment()).append("\n");
                }
            }
        }
        String result = sb.toString().trim();
        return result.isEmpty()
                ? "Автоматически перенесено из группы #" + group.getId() + " при смене темы"
                : result;
    }


    /**
     * Добавить участника в группу.
     * Только владелец группы или модератор/администратор могут это сделать.
     */
    @Transactional
    public CourseGroup addMemberToGroup(Long courseId, Long groupId, Long accountId, Account requester) {
        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));
        if (!group.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Группа не принадлежит данному курсу");
        }
        boolean isOwner = group.getMembers().stream()
                .anyMatch(m -> m.getAccount().getId().equals(requester.getId())
                        && Boolean.TRUE.equals(m.getIsOwner()));
        boolean isMod = requester.getRole() != null
                && (requester.getRole().getName() == org.diplom_backend.model.Role.ROLE_MODERATOR
                || requester.getRole().getName() == org.diplom_backend.model.Role.ROLE_ADMIN);
        if (!isOwner && !isMod) {
            throw new NotEnoughRightsException("Только владелец группы или модератор может добавлять участников");
        }
        boolean alreadyIn = group.getMembers().stream()
                .anyMatch(m -> m.getAccount().getId().equals(accountId));
        if (alreadyIn) {
            throw new IllegalStateException("Пользователь уже состоит в этой группе");
        }
        Account newMember = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователь", "id", accountId.toString()));
        CourseGroupMember member = CourseGroupMember.builder()
                .group(group)
                .account(newMember)
                .isOwner(false)
                .joinedAt(java.time.LocalDateTime.now())
                .build();
        courseGroupMemberRepository.save(member);
        return courseGroupRepository.findById(groupId).orElse(group);
    }

    /**
     * Удалить участника из группы.
     * Нельзя удалить владельца. Только владелец группы или модератор/администратор.
     */
    @Transactional
    public CourseGroup removeMemberFromGroup(Long courseId, Long groupId, Long accountId, Account requester) {
        CourseGroup group = courseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityModelNotFoundException("Группа", "id", groupId.toString()));
        if (!group.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Группа не принадлежит данному курсу");
        }

        CourseGroupMember target = courseGroupMemberRepository
                .findByGroup_IdAndAccount_Id(groupId, accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Участник", "accountId", accountId.toString()));

        if (Boolean.TRUE.equals(target.getIsOwner())) {
            throw new IllegalStateException("Нельзя удалить владельца группы");
        }

        boolean isOwner = courseGroupMemberRepository
                .findByGroup_IdAndAccount_Id(groupId, requester.getId())
                .map(m -> Boolean.TRUE.equals(m.getIsOwner()))
                .orElse(false);

        boolean isMod = requester.getRole() != null
                && (requester.getRole().getName() == org.diplom_backend.model.Role.ROLE_MODERATOR
                || requester.getRole().getName() == org.diplom_backend.model.Role.ROLE_ADMIN);

        if (!isOwner && !isMod) {
            throw new NotEnoughRightsException("Только владелец группы или модератор может удалять участников");
        }

        courseGroupMemberRepository.deleteByGroup_IdAndAccount_Id(groupId, accountId);
        return courseGroupRepository.findById(groupId).orElse(group);
    }

    private void autoSubmitTopicApproval(CourseEntity course, CourseGroup group, String topicTitle) {
        courseLessonRepository.findByCourse_IdOrderByOrderNumberAsc(course.getId()).stream()
                .filter(l -> l.getCategory() == LessonCategory.HEARING
                        && l.getHearingStage() == HearingStage.TOPIC_APPROVAL)
                .findFirst()
                .ifPresent(topicLesson -> {
                    if (hearingSubmissionRepository.findByLesson_IdAndGroup_Id(topicLesson.getId(), group.getId()).isEmpty()) {
                        HearingSubmission submission = HearingSubmission.builder()
                                .lesson(topicLesson)
                                .group(group)
                                .status(ProjectStatus.ON_REVIEW)
                                .currentVersion(1)
                                .submittedAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        hearingSubmissionRepository.save(submission);
                    }
                });
    }
}