package org.diplom_backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.dto.requests.GradeSubmissionRequestDto;
import org.diplom_backend.dto.responses.CriterionSnapshotEntryDto;
import org.diplom_backend.dto.responses.SubmissionReviewHistoryResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.*;
import org.diplom_backend.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.diplom_backend.dto.responses.CourseSummaryResponseDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final LessonSubmissionRepository submissionRepository;
    private final SchoolRepository schoolRepository;
    private final AccountRepository accountRepository;
    private final FileService fileService;
    private final GradingCriterionRepository criterionRepository;
    private final SubmissionCriterionGradeRepository criterionGradeRepository;
    private final SubmissionReviewHistoryRepository submissionReviewHistoryRepository;
    private final CourseModeratorService courseModeratorService;
    private final CourseModeratorRepository courseModeratorRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final CourseGroupMemberRepository courseGroupMemberRepository;
    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final HearingReviewRepository hearingReviewRepository;
    private final ObjectMapper objectMapper;
    private final TelegramNotificationService telegramNotificationService;
    private final LaggingCheckService laggingCheckService;
    private final StudentCourseEnrollmentService enrollmentService;

    // ──────────────────────────────────────────────────────────────────────────
    // Курсы
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public CourseEntity createCourse(String name, String description, Long schoolId,
                                     Boolean isIntroduction, Boolean forLaggingStudents) {
        CourseEntity course = CourseEntity.builder()
                .name(name)
                .description(description)
                .isIntroduction(Boolean.TRUE.equals(isIntroduction))
                .forLaggingStudents(Boolean.TRUE.equals(forLaggingStudents))
                .build();

        if (schoolId != null) {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));
            course.getSchools().add(school);
            log.info("Создан курс '{}' с привязкой к школе '{}'", name, school.getName());
        } else {
            log.info("Создан курс '{}' без привязки к школе", name);
        }

        CourseEntity saved = courseRepository.save(course);
        // Для вводного курса этапы слушаний не создаются — только уроки категории LESSON
        if (!Boolean.TRUE.equals(isIntroduction)) {
            createDefaultHearingLessons(saved);
        }
        return saved;
    }

    /** Обратная совместимость */
    @Transactional
    public CourseEntity createCourse(String name, String description, Long schoolId) {
        return createCourse(name, description, schoolId, false, false);
    }

    private void createDefaultHearingLessons(CourseEntity course) {
        HearingStage[] stages = HearingStage.values();
        for (int i = 0; i < stages.length; i++) {
            HearingStage stage = stages[i];
            CourseLessonEntity lesson = CourseLessonEntity.builder()
                    .course(course)
                    .title(stage.getDescription())
                    .category(LessonCategory.HEARING)
                    .hearingStage(stage)
                    .submissionType(SubmissionType.FILE)
                    .maxScore(0)
                    .orderNumber(901 + i)
                    .hearingOpenForStudents(stage == HearingStage.TOPIC_APPROVAL)
                    .build();
            lessonRepository.save(lesson);
        }
        log.info("Созданы {} этапов слушаний для курса '{}'", stages.length, course.getName());
    }

    private static boolean computeDefaultHearingOpen(HearingStage stage, Boolean explicit) {
        if (explicit != null) {
            return explicit;
        }
        return stage == null || stage == HearingStage.TOPIC_APPROVAL;
    }

    @Transactional(readOnly = true)
    public CourseEntity getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));
    }

    @Transactional(readOnly = true)
    public Page<CourseEntity> getCoursesBySchool(Long schoolId, int page, int size) {
        return courseRepository.findBySchools_Id(schoolId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Page<CourseEntity> getAllActiveCourses(int page, int size) {
        return courseRepository.findByIsActiveTrue(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Page<CourseEntity> getAllCourses(int page, int size) {
        return courseRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<CourseEntity> searchCourses(String query, int page, int size) {
        return courseRepository.searchByName(query,
                PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<CourseEntity> getMySchoolCourses(Account account) {
        Account fresh = accountRepository.findById(account.getId()).orElse(null);
        if (fresh == null || fresh.getSchool() == null) {
            return List.of();
        }

        Long schoolId = fresh.getSchool().getId();
        List<CourseEntity> all = courseRepository.findBySchools_IdAndIsActiveTrue(schoolId);

        log.info("=== getMySchoolCourses для ученика {} ===", fresh.getNickname());
        log.info("Школа: {}, курсов в школе: {}", schoolId, all.size());

        if (Boolean.TRUE.equals(fresh.getIsLagging())) {
            log.info("Ученик отстающий, возвращаем вводные и lagging курсы");
            return all.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsIntroduction())
                            || Boolean.TRUE.equals(c.getForLaggingStudents()))
                    .collect(Collectors.toList());
        }

        Set<Long> enrolledCourseIds = courseGroupMemberRepository.findAllByAccount(fresh.getId())
                .stream()
                .map(m -> m.getGroup().getCourse().getId())
                .collect(Collectors.toSet());

        log.info("Курсы через группы: {}", enrolledCourseIds);

        Set<Long> forcedCourseIds = new HashSet<>();
        try {
            forcedCourseIds = enrollmentService.getForcedCourseIds(fresh.getId());
            log.info("Принудительные курсы из enrollmentService: {}", forcedCourseIds);
        } catch (Exception e) {
            log.error("Ошибка получения forcedCourseIds: {}", e.getMessage(), e);
        }

        Set<Long> allAccessibleCourseIds = new HashSet<>();
        allAccessibleCourseIds.addAll(enrolledCourseIds);
        allAccessibleCourseIds.addAll(forcedCourseIds);

        log.info("Все доступные ID курсов: {}", allAccessibleCourseIds);

        List<CourseEntity> result = new ArrayList<>(all.stream()
                .filter(c -> {
                    if (Boolean.TRUE.equals(c.getForLaggingStudents())) return false;
                    if (Boolean.TRUE.equals(c.getIsIntroduction())) return true;
                    boolean accessible = allAccessibleCourseIds.contains(c.getId());
                    log.debug("Курс {} (id={}): accessible={}", c.getName(), c.getId(), accessible);
                    return accessible;
                })
                .collect(Collectors.toList()));

        // Добавляем принудительные курсы, которых нет в школе
        if (!forcedCourseIds.isEmpty()) {
            Set<Long> alreadyIncluded = result.stream().map(CourseEntity::getId).collect(Collectors.toSet());
            List<CourseEntity> forcedCourses = courseRepository.findAllById(forcedCourseIds).stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                    .filter(c -> !Boolean.TRUE.equals(c.getForLaggingStudents()))
                    .filter(c -> !Boolean.TRUE.equals(c.getIsIntroduction()))
                    .filter(c -> !alreadyIncluded.contains(c.getId()))
                    .toList();
            log.info("Добавляем принудительные курсы вне школы: {}", forcedCourses.stream().map(CourseEntity::getName).toList());
            result.addAll(forcedCourses);
        }

        log.info("Итоговые курсы для ученика {}: {}", fresh.getNickname(), result.stream().map(CourseEntity::getName).toList());
        return result;
    }

    @Transactional(readOnly = true)
    public CourseSummaryResponseDto getCourseSummary(Long courseId) {
        CourseEntity course = getCourse(courseId);
        List<CourseLessonEntity> lessons = lessonRepository.findByCourse_IdOrderByOrderNumberAsc(courseId);

        List<CourseSummaryResponseDto.LessonHeader> headers = lessons.stream()
                .map(l -> CourseSummaryResponseDto.LessonHeader.builder()
                        .id(l.getId())
                        .title(l.getTitle())
                        .maxScore(l.getMaxScore() != null ? l.getMaxScore() : 100)
                        .category(l.getCategory() != null ? l.getCategory().name() : "LESSON")
                        .build())
                .toList();

        List<LessonSubmissionEntity> allSubs = submissionRepository.findAllByCourseId(courseId);
        Map<Long, Map<Long, LessonSubmissionEntity>> regularSubsByAccountAndLesson = allSubs.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getAccount().getId(),
                        Collectors.toMap(s -> s.getLesson().getId(), Function.identity(), (a, b) -> a)
                ));

        List<CourseGroupMember> members = courseGroupMemberRepository.findAllByCourseId(courseId);
        Map<Long, CourseGroup> groupByAccountId = members.stream()
                .collect(Collectors.toMap(m -> m.getAccount().getId(), CourseGroupMember::getGroup, (a, b) -> a));

        List<HearingSubmission> hearingSubs = hearingSubmissionRepository.findAllByCourseIdWithReviews(courseId);
        Map<Long, Map<Long, HearingSubmission>> hearingSubByGroupAndLesson = hearingSubs.stream()
                .collect(Collectors.groupingBy(
                        hs -> hs.getGroup().getId(),
                        Collectors.toMap(hs -> hs.getLesson().getId(), Function.identity(), (a, b) -> a)
                ));

        // Собираем ID учеников в зависимости от типа курса:
        // — Вводный (isIntroduction=true): все ученики привязанных школ
        // — Целевой / для отстающих: только через группы или принудительное назначение
        Set<Long> assignedAccountIds = new HashSet<>();

        if (Boolean.TRUE.equals(course.getIsIntroduction())) {
            // Для вводного курса берём всех учеников из привязанных школ
            for (var school : course.getSchools()) {
                // findBySchoolId возвращает Page — берём большую страницу чтобы охватить всех
                org.springframework.data.domain.Page<Account> page =
                        accountRepository.findBySchoolId(school.getId(),
                                org.springframework.data.domain.PageRequest.of(0, 5000));
                for (Account student : page.getContent()) {
                    // Включаем только пользователей (не модераторов, не администраторов)
                    if (student.getRole() != null
                            && org.diplom_backend.model.Role.ROLE_USER.equals(student.getRole().getName())) {
                        assignedAccountIds.add(student.getId());
                    }
                }
            }
        } else {
            // 1. Ученики через группы
            for (Long accountId : groupByAccountId.keySet()) {
                Account student = accountRepository.findById(accountId).orElse(null);
                if (student != null && !Boolean.TRUE.equals(student.getIsLagging())) {
                    assignedAccountIds.add(accountId);
                }
            }

            // 2. Ученики через принудительное назначение (только не отстающие)
            Set<Long> forcedStudentIds = enrollmentService.getStudentIdsForcedToCourse(courseId);
            for (Long studentId : forcedStudentIds) {
                Account student = accountRepository.findById(studentId).orElse(null);
                if (student != null && !Boolean.TRUE.equals(student.getIsLagging())) {
                    assignedAccountIds.add(studentId);
                }
            }
        }

        if (assignedAccountIds.isEmpty()) {
            return CourseSummaryResponseDto.builder()
                    .lessons(headers)
                    .students(List.of())
                    .build();
        }

        Map<Long, Account> accountById = accountRepository.findAllById(assignedAccountIds).stream()
                .collect(Collectors.toMap(Account::getId, Function.identity(), (a, b) -> a));
        List<CourseSummaryResponseDto.StudentRow> rows = new ArrayList<>();
        for (Long accountId : assignedAccountIds) {
            Account student = accountById.get(accountId);
            if (student == null) continue;

            Map<Long, LessonSubmissionEntity> subsByLesson =
                    regularSubsByAccountAndLesson.getOrDefault(accountId, Map.of());

            CourseGroup group = groupByAccountId.get(accountId);
            Long groupId = group != null ? group.getId() : null;
            Map<Long, HearingSubmission> hsByLesson = (groupId != null)
                    ? hearingSubByGroupAndLesson.getOrDefault(groupId, Map.of())
                    : Map.of();

            List<CourseSummaryResponseDto.LessonScore> scores = lessons.stream()
                    .map(l -> {
                        if (l.getCategory() == LessonCategory.HEARING) {
                            HearingSubmission hs = hsByLesson.get(l.getId());
                            Integer maxGrade = null;
                            if (hs != null && hs.getReviews() != null) {
                                int curV = hs.getCurrentVersion() != null ? hs.getCurrentVersion() : 1;
                                for (HearingReview r : hs.getReviews()) {
                                    if (r.getGrade() == null) continue;
                                    Integer rv = r.getSubmissionVersion();
                                    if (rv != null && rv != curV) continue;
                                    maxGrade = (maxGrade == null) ? r.getGrade() : Math.max(maxGrade, r.getGrade());
                                }
                            }
                            return CourseSummaryResponseDto.LessonScore.builder()
                                    .lessonId(l.getId())
                                    .status(hs != null && hs.getStatus() != null ? hs.getStatus().name() : null)
                                    .score(maxGrade)
                                    .build();
                        }

                        LessonSubmissionEntity sub = subsByLesson.get(l.getId());
                        return CourseSummaryResponseDto.LessonScore.builder()
                                .lessonId(l.getId())
                                .status(sub != null && sub.getStatus() != null ? sub.getStatus().name() : null)
                                .score(sub != null ? sub.getScore() : null)
                                .build();
                    })
                    .toList();

            int totalScore = scores.stream()
                    .filter(s -> s.getScore() != null)
                    .mapToInt(CourseSummaryResponseDto.LessonScore::getScore)
                    .sum();

            rows.add(CourseSummaryResponseDto.StudentRow.builder()
                    .accountId(student.getId())
                    .nickname(student.getNickname())
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .schoolName(student.getSchool() != null ? student.getSchool().getName() : null)
                    .className(student.getSchoolClass() != null ? student.getSchoolClass().getName() : null)
                    .isLagging(Boolean.TRUE.equals(student.getIsLagging()))
                    .scores(scores)
                    .totalScore(totalScore)
                    .build());
        }

        rows.sort(Comparator.comparingInt(CourseSummaryResponseDto.StudentRow::getTotalScore).reversed());

        return CourseSummaryResponseDto.builder()
                .lessons(headers)
                .students(rows)
                .build();
    }

    @Transactional
    public CourseEntity toggleCourseActive(Long courseId) {
        CourseEntity course = getCourse(courseId);
        course.setIsActive(!course.getIsActive());
        CourseEntity saved = courseRepository.save(course);
        if (Boolean.TRUE.equals(saved.getIsActive()) && Boolean.TRUE.equals(saved.getForLaggingStudents())) {
            laggingCheckService.enrollLaggingStudentsForCourse(courseId);
        }
        return saved;
    }

    @Transactional
    public CourseEntity setForLaggingStudents(Long courseId, boolean value) {
        CourseEntity course = getCourse(courseId);
        course.setForLaggingStudents(value);
        CourseEntity saved = courseRepository.save(course);
        if (value && Boolean.TRUE.equals(saved.getIsActive())) {
            laggingCheckService.enrollLaggingStudentsForCourse(courseId);
        }
        return saved;
    }

    @Transactional
    public CourseEntity setIsIntroduction(Long courseId, boolean value) {
        CourseEntity course = getCourse(courseId);
        course.setIsIntroduction(value);
        return courseRepository.save(course);
    }

    @Transactional
    public CourseEntity addSchoolToCourse(Long courseId, Long schoolId) {
        CourseEntity course = getCourse(courseId);
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));
        course.getSchools().add(school);
        log.info("Школа '{}' привязана к курсу '{}'", school.getName(), course.getName());
        CourseEntity saved = courseRepository.save(course);
        laggingCheckService.enrollLaggingStudentsForCourse(courseId);
        return saved;
    }

    @Transactional
    public CourseEntity removeSchoolFromCourse(Long courseId, Long schoolId) {
        CourseEntity course = getCourse(courseId);
        boolean removed = course.getSchools().removeIf(s -> s.getId().equals(schoolId));
        if (!removed) {
            throw new EntityModelNotFoundException("Школа в курсе", "schoolId", schoolId.toString());
        }
        log.info("Школа id={} откреплена от курса '{}'", schoolId, course.getName());
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Page<LessonSubmissionEntity> getSubmissionsByCourse(Long courseId, String statusFilter, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (statusFilter != null && !statusFilter.isBlank()) {
            SubmissionStatus status = SubmissionStatus.valueOf(statusFilter);
            return submissionRepository.findByCourseIdAndStatus(courseId, status, pageRequest);
        }
        return submissionRepository.findByCourseId(courseId, pageRequest);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Уроки
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public CourseLessonEntity addLesson(Long courseId, String title, String lectureContent,
                                        String practiceDescription, SubmissionType submissionType,
                                        Integer orderNumber, String videoUrl, Integer maxScore,
                                        LocalDateTime submissionDeadline,
                                        LessonCategory category, HearingStage hearingStage,
                                        Boolean hearingOpenForStudents) {
        CourseEntity course = getCourse(courseId);

        if (lessonRepository.existsByCourse_IdAndOrderNumber(courseId, orderNumber)) {
            throw new IllegalStateException(
                    "Урок с порядковым номером " + orderNumber + " уже существует в данном курсе");
        }

        LessonCategory cat = category != null ? category : LessonCategory.LESSON;
        CourseLessonEntity lesson = CourseLessonEntity.builder()
                .course(course)
                .title(title)
                .lectureContent(lectureContent)
                .practiceDescription(practiceDescription)
                .submissionType(submissionType)
                .orderNumber(orderNumber)
                .videoUrl(videoUrl)
                .maxScore(maxScore != null ? maxScore : 100)
                .submissionDeadline(submissionDeadline)
                .category(cat)
                .hearingStage(hearingStage)
                .hearingOpenForStudents(cat != LessonCategory.HEARING
                        || computeDefaultHearingOpen(hearingStage, hearingOpenForStudents))
                .build();

        CourseLessonEntity saved = lessonRepository.save(lesson);
        log.info("Добавлен урок '{}' (#{}) в курс '{}'", title, orderNumber, course.getName());
        return saved;
    }

    @Transactional
    public CourseLessonEntity updateLesson(Long courseId, Long lessonId,
                                           String title, String lectureContent,
                                           String practiceDescription, SubmissionType submissionType,
                                           String videoUrl, Integer maxScore,
                                           LocalDateTime submissionDeadline,
                                           LessonCategory category, HearingStage hearingStage,
                                           Boolean hearingOpenForStudents) {
        CourseLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityModelNotFoundException("Урок", "id", lessonId.toString()));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Урок не принадлежит данному курсу");
        }

        if (title != null) lesson.setTitle(title);
        if (lectureContent != null) lesson.setLectureContent(lectureContent);
        if (practiceDescription != null) lesson.setPracticeDescription(practiceDescription);
        if (submissionType != null) lesson.setSubmissionType(submissionType);
        if (videoUrl != null) lesson.setVideoUrl(videoUrl);
        if (maxScore != null) lesson.setMaxScore(maxScore);
        if (category != null) lesson.setCategory(category);
        if (hearingStage != null) lesson.setHearingStage(hearingStage);
        lesson.setSubmissionDeadline(submissionDeadline);

        if (lesson.getCategory() == LessonCategory.HEARING) {
            if (lesson.getHearingStage() == HearingStage.TOPIC_APPROVAL) {
                lesson.setHearingOpenForStudents(true);
            } else if (hearingOpenForStudents != null) {
                lesson.setHearingOpenForStudents(hearingOpenForStudents);
            }
        } else {
            lesson.setHearingOpenForStudents(true);
        }

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long courseId, Long lessonId) {
        CourseLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityModelNotFoundException("Урок", "id", lessonId.toString()));
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Урок не принадлежит данному курсу");
        }
        List<LessonSubmissionEntity> subs = submissionRepository.findAllByLesson_Id(lessonId);
        for (LessonSubmissionEntity s : subs) {
            criterionGradeRepository.deleteAllBySubmission_Id(s.getId());
        }
        submissionRepository.deleteAll(subs);
        criterionRepository.deleteAllByLesson_Id(lessonId);
        lessonRepository.delete(lesson);
        log.info("Удалён урок id={} из курса id={}", lessonId, courseId);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        List<CourseLessonEntity> lessons = lessonRepository.findByCourse_IdOrderByOrderNumberAsc(courseId);
        for (CourseLessonEntity lesson : lessons) {
            List<HearingSubmission> hearingSubs = hearingSubmissionRepository.findByLesson_Id(lesson.getId());
            for (HearingSubmission hs : hearingSubs) {
                hearingReviewRepository.deleteAll(hs.getReviews());
            }
            hearingSubmissionRepository.deleteAll(hearingSubs);

            List<LessonSubmissionEntity> subs = submissionRepository.findAllByLesson_Id(lesson.getId());
            for (LessonSubmissionEntity s : subs) {
                criterionGradeRepository.deleteAllBySubmission_Id(s.getId());
            }
            submissionRepository.deleteAll(subs);
            criterionRepository.deleteAllByLesson_Id(lesson.getId());
        }

        List<CourseGroup> groups = courseGroupRepository.findByCourse_Id(courseId);
        for (CourseGroup g : groups) {
            courseGroupMemberRepository.deleteAll(g.getMembers());
        }
        courseGroupRepository.deleteAll(groups);

        courseModeratorRepository.deleteByCourse_Id(courseId);

        courseRepository.delete(course);
        log.info("Удалён курс id={} «{}»", courseId, course.getName());
    }

    @Transactional(readOnly = true)
    public CourseLessonEntity getLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityModelNotFoundException("Урок", "id", lessonId.toString()));
    }

    @Transactional(readOnly = true)
    public List<CourseLessonEntity> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourse_IdOrderByOrderNumberAsc(courseId);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Сдача работ
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public LessonSubmissionEntity submitWork(Long lessonId, String textContent,
                                             MultipartFile file, Account student) {
        CourseLessonEntity lesson = getLesson(lessonId);
        assertDeadlineAllowsSubmission(lesson);
        validateSubmissionContent(lesson.getSubmissionType(), textContent, file);

        submissionRepository.findByLesson_IdAndAccount_Id(lessonId, student.getId())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Вы уже отправили работу по этому заданию. Используйте обновление.");
                });

        FileEntity fileEntity = null;
        if (file != null && !file.isEmpty()) {
            fileEntity = fileService.store(file);
        }

        LessonSubmissionEntity submission = LessonSubmissionEntity.builder()
                .lesson(lesson)
                .account(student)
                .textContent(textContent)
                .file(fileEntity)
                .status(SubmissionStatus.SUBMITTED)
                .build();

        LessonSubmissionEntity saved = submissionRepository.save(submission);
        log.info("Ученик {} сдал работу по уроку '{}'", student.getNickname(), lesson.getTitle());
        Account freshStudent = accountRepository.findById(student.getId()).orElse(student);
        String fio = ((freshStudent.getLastName() != null ? freshStudent.getLastName() + " " : "")
                + (freshStudent.getFirstName() != null ? freshStudent.getFirstName() : "")).trim();
        telegramNotificationService.notifyModeratorsAboutLessonSubmission(
                lesson.getCourse().getId(),
                lesson.getCourse().getName(),
                lesson.getTitle(),
                freshStudent.getNickname(),
                fio.isBlank() ? null : fio,
                freshStudent.getSchool() != null ? freshStudent.getSchool().getName() : null,
                freshStudent.getSchoolClass() != null ? freshStudent.getSchoolClass().getName() : null,
                freshStudent.getId()
        );
        return saved;
    }

    @Transactional
    public LessonSubmissionEntity updateSubmission(Long lessonId, String textContent,
                                                   MultipartFile file, Account student) {
        LessonSubmissionEntity submission = submissionRepository
                .findByLesson_IdAndAccount_Id(lessonId, student.getId())
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "lessonId+accountId", lessonId + "+" + student.getId()));

        if (submission.getStatus() == SubmissionStatus.ACCEPTED) {
            throw new IllegalStateException("Нельзя изменить уже принятую работу");
        }

        CourseLessonEntity lesson = submission.getLesson();
        assertDeadlineAllowsSubmission(lesson);
        validateSubmissionContent(lesson.getSubmissionType(), textContent, file);

        if (textContent != null) {
            submission.setTextContent(textContent);
        }
        if (file != null && !file.isEmpty()) {
            submission.setFile(fileService.store(file));
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setReviewerComment(null);
        submission.setScore(null);
        LessonSubmissionEntity saved = submissionRepository.save(submission);
        Account freshStudent = accountRepository.findById(student.getId()).orElse(student);
        String fio = ((freshStudent.getLastName() != null ? freshStudent.getLastName() + " " : "")
                + (freshStudent.getFirstName() != null ? freshStudent.getFirstName() : "")).trim();
        telegramNotificationService.notifyModeratorsAboutLessonSubmission(
                submission.getLesson().getCourse().getId(),
                submission.getLesson().getCourse().getName(),
                submission.getLesson().getTitle(),
                freshStudent.getNickname(),
                fio.isBlank() ? null : fio,
                freshStudent.getSchool() != null ? freshStudent.getSchool().getName() : null,
                freshStudent.getSchoolClass() != null ? freshStudent.getSchoolClass().getName() : null,
                freshStudent.getId()
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public LessonSubmissionEntity getMySubmission(Long lessonId, Long accountId) {
        return submissionRepository.findByLesson_IdAndAccount_Id(lessonId, accountId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<LessonSubmissionEntity> getMySubmissionsForCourse(Long courseId, Long accountId) {
        return submissionRepository.findByCourseAndAccount(courseId, accountId);
    }

    @Transactional(readOnly = true)
    public Page<LessonSubmissionEntity> getSubmissionsByLesson(Long lessonId, int page, int size) {
        return submissionRepository.findByLesson_Id(lessonId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt")));
    }

    @Transactional(readOnly = true)
    public int getLessonMaxScoreConsideringCriteria(Long lessonId) {
        CourseLessonEntity lesson = getLesson(lessonId);
        List<GradingCriterion> criteria = criterionRepository.findByLesson_IdOrderByOrderNumberAsc(lessonId);
        if (criteria != null && !criteria.isEmpty()) {
            return criteria.stream()
                    .mapToInt(GradingCriterion::getMaxPoints)
                    .sum();
        }
        return lesson.getMaxScore() != null ? lesson.getMaxScore() : 0;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Проверка работ (модератор)
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public LessonSubmissionEntity reviewSubmission(Long submissionId, SubmissionStatus newStatus,
                                                   String reviewerComment, Integer score,
                                                   Account reviewer) {
        LessonSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "id", submissionId.toString()));

        courseModeratorService.verifyModeratorAccessToCourse(
                reviewer, submission.getLesson().getCourse().getId());

        if (score != null && submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "Балл можно выставить только пока работа на проверке (ожидает проверки)");
        }

        if (newStatus == SubmissionStatus.SUBMITTED) {
            throw new IllegalArgumentException("Нельзя установить статус SUBMITTED при проверке");
        }

        submission.setStatus(newStatus);
        submission.setReviewerComment(reviewerComment);
        if (score != null) {
            int maxScore = getLessonMaxScoreConsideringCriteria(submission.getLesson().getId());
            if (score < 0 || score > maxScore) {
                throw new IllegalArgumentException("Балл должен быть от 0 до " + maxScore);
            }
            submission.setScore(score);
        }
        LessonSubmissionEntity saved = submissionRepository.save(submission);
        recordReviewHistory(saved, reviewer, null);
        telegramNotificationService.notifyStudentAboutLessonReview(
                saved.getAccount().getId(),
                saved.getLesson().getCourse().getName(),
                saved.getLesson().getTitle(),
                saved.getStatus().name(),
                saved.getScore(),
                saved.getReviewerComment()
        );
        return saved;
    }

    @Transactional
    public CourseLessonEntity uploadLectureFile(Long courseId, Long lessonId, MultipartFile file) {
        CourseLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityModelNotFoundException("Урок", "id", lessonId.toString()));

        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Урок не принадлежит данному курсу");
        }

        FileEntity fileEntity = fileService.store(file);
        lesson.setLectureFile(fileEntity);
        return lessonRepository.save(lesson);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Критерии оценивания (привязаны к уроку)
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<GradingCriterion> getCriteriaByLesson(Long lessonId) {
        return criterionRepository.findByLesson_IdOrderByOrderNumberAsc(lessonId);
    }

    @Transactional
    public List<GradingCriterion> setLessonCriteria(Long lessonId,
                                                    List<org.diplom_backend.dto.requests.CreateGradingCriterionRequestDto> dtos) {
        CourseLessonEntity lesson = getLesson(lessonId);
        criterionRepository.deleteAllByLesson_Id(lessonId);
        criterionRepository.flush();

        int totalMaxPoints = 0;
        for (int i = 0; i < dtos.size(); i++) {
            var dto = dtos.get(i);
            GradingCriterion criterion = GradingCriterion.builder()
                    .lesson(lesson)
                    .orderNumber(i + 1)
                    .name(dto.name())
                    .description(dto.description())
                    .maxPoints(dto.maxPoints())
                    .build();
            criterionRepository.save(criterion);
            totalMaxPoints += dto.maxPoints();
        }

        if (totalMaxPoints > 0) {
            lesson.setMaxScore(totalMaxPoints);
            lessonRepository.save(lesson);
        }

        return criterionRepository.findByLesson_IdOrderByOrderNumberAsc(lessonId);
    }

    @Transactional(readOnly = true)
    public List<SubmissionCriterionGrade> getCriterionGrades(Long submissionId) {
        return criterionGradeRepository.findBySubmission_Id(submissionId);
    }

    @Transactional
    public LessonSubmissionEntity gradeSubmissionByCriteria(Long submissionId,
                                                            GradeSubmissionRequestDto request,
                                                            Account reviewer) {
        LessonSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "id", submissionId.toString()));

        courseModeratorService.verifyModeratorAccessToCourse(
                reviewer, submission.getLesson().getCourse().getId());

        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "Оценивание по критериям доступно только пока работа на проверке");
        }

        Long lessonId = submission.getLesson().getId();
        List<GradingCriterion> criteria = criterionRepository.findByLesson_IdOrderByOrderNumberAsc(lessonId);

        Map<Long, GradingCriterion> criterionMap = criteria.stream()
                .collect(Collectors.toMap(GradingCriterion::getId, Function.identity()));

        criterionGradeRepository.deleteAllBySubmission_Id(submissionId);
        criterionGradeRepository.flush();

        int totalScore = 0;
        for (GradeSubmissionRequestDto.CriterionScore gs : request.grades()) {
            GradingCriterion criterion = criterionMap.get(gs.criterionId());
            if (criterion == null) {
                throw new IllegalArgumentException("Критерий с id=" + gs.criterionId() + " не найден для урока");
            }
            if (gs.points() > criterion.getMaxPoints()) {
                throw new IllegalArgumentException(
                        "Балл " + gs.points() + " превышает максимум " + criterion.getMaxPoints()
                                + " для критерия '" + criterion.getName() + "'");
            }
            SubmissionCriterionGrade grade = SubmissionCriterionGrade.builder()
                    .submission(submission)
                    .criterion(criterion)
                    .points(gs.points())
                    .build();
            criterionGradeRepository.save(grade);
            totalScore += gs.points();
        }

        submission.setScore(totalScore);
        // Статус берётся из запроса; если не передан — по умолчанию ACCEPTED
        SubmissionStatus targetStatus = request.newStatus() != null
                ? request.newStatus()
                : SubmissionStatus.ACCEPTED;
        if (targetStatus == SubmissionStatus.SUBMITTED) {
            throw new IllegalArgumentException("Нельзя установить статус SUBMITTED при проверке");
        }
        submission.setStatus(targetStatus);
        if (request.reviewerComment() != null) {
            submission.setReviewerComment(request.reviewerComment());
        }

        LessonSubmissionEntity saved = submissionRepository.save(submission);
        String criteriaJson = buildCriteriaSnapshotJson(request, criterionMap);
        recordReviewHistory(saved, reviewer, criteriaJson);
        telegramNotificationService.notifyStudentAboutLessonReview(
                saved.getAccount().getId(),
                saved.getLesson().getCourse().getName(),
                saved.getLesson().getTitle(),
                saved.getStatus().name(),
                saved.getScore(),
                saved.getReviewerComment()
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public List<SubmissionReviewHistoryResponseDto> getSubmissionReviewHistory(Long submissionId, Account viewer) {
        LessonSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "id", submissionId.toString()));
        Long courseId = submission.getLesson().getCourse().getId();
        if (!submission.getAccount().getId().equals(viewer.getId())) {
            courseModeratorService.verifyModeratorAccessToCourse(viewer, courseId);
        }
        return submissionReviewHistoryRepository.findBySubmission_IdOrderByCreatedAtAsc(submissionId).stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Transactional
    public SubmissionReviewHistoryResponseDto addStudentSubmissionReply(Long submissionId, String comment,
                                                                        Account student) {
        LessonSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "id", submissionId.toString()));
        if (!submission.getAccount().getId().equals(student.getId())) {
            throw new NotEnoughRightsException("Можно отвечать только по своей работе");
        }
        String trimmed = comment != null ? comment.trim() : "";
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Текст ответа не может быть пустым");
        }
        Account authorRef = accountRepository.getReferenceById(student.getId());
        SubmissionReviewHistoryEntity row = SubmissionReviewHistoryEntity.builder()
                .submission(submission)
                .reviewer(authorRef)
                .entryKind(SubmissionReviewHistoryKind.STUDENT_REPLY)
                .statusAfter(submission.getStatus())
                .score(null)
                .reviewerComment(trimmed)
                .criteriaSnapshotJson(null)
                .build();
        SubmissionReviewHistoryEntity saved = submissionReviewHistoryRepository.save(row);
        telegramNotificationService.notifyModeratorsAboutLessonComment(
                submission.getLesson().getCourse().getId(),
                submission.getLesson().getCourse().getName(),
                submission.getLesson().getTitle(),
                student.getNickname(),
                trimmed
        );
        return toHistoryDto(saved);
    }

    /**
     * Добавить комментарий модератора без изменения оценки.
     */
    @Transactional
    public SubmissionReviewHistoryResponseDto addModeratorComment(Long submissionId, String comment,
                                                                  Account moderator) {
        LessonSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityModelNotFoundException(
                        "Работа", "id", submissionId.toString()));
        courseModeratorService.verifyModeratorAccessToCourse(
                moderator, submission.getLesson().getCourse().getId());
        String trimmed = comment != null ? comment.trim() : "";
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }
        Account reviewerRef = accountRepository.getReferenceById(moderator.getId());
        SubmissionReviewHistoryEntity row = SubmissionReviewHistoryEntity.builder()
                .submission(submission)
                .reviewer(reviewerRef)
                .entryKind(SubmissionReviewHistoryKind.MODERATOR)
                .statusAfter(submission.getStatus())
                .score(null)
                .reviewerComment(trimmed)
                .criteriaSnapshotJson(null)
                .build();
        SubmissionReviewHistoryEntity saved = submissionReviewHistoryRepository.save(row);
        telegramNotificationService.notifyStudentAboutLessonComment(
                submission.getAccount().getId(),
                submission.getLesson().getCourse().getName(),
                submission.getLesson().getTitle(),
                trimmed
        );
        return toHistoryDto(saved);
    }

    private SubmissionReviewHistoryResponseDto toHistoryDto(SubmissionReviewHistoryEntity entity) {
        SubmissionReviewHistoryKind kind = entity.getEntryKind() != null
                ? entity.getEntryKind()
                : SubmissionReviewHistoryKind.MODERATOR;
        return new SubmissionReviewHistoryResponseDto(
                entity.getId(),
                kind,
                entity.getStatusAfter(),
                entity.getScore(),
                entity.getReviewerComment(),
                entity.getCreatedAt(),
                entity.getReviewer() != null ? entity.getReviewer().getNickname() : null,
                parseCriterionSnapshot(entity.getCriteriaSnapshotJson())
        );
    }

    private List<CriterionSnapshotEntryDto> parseCriterionSnapshot(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CriterionSnapshotEntryDto>>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("Не удалось разобрать criteria_snapshot_json: {}", e.getMessage());
            return List.of();
        }
    }

    private String buildCriteriaSnapshotJson(GradeSubmissionRequestDto request,
                                             Map<Long, GradingCriterion> criterionMap) {
        List<CriterionSnapshotEntryDto> rows = request.grades().stream().map(gs -> {
            GradingCriterion c = criterionMap.get(gs.criterionId());
            return new CriterionSnapshotEntryDto(
                    gs.criterionId(),
                    c != null ? c.getName() : "",
                    c != null ? c.getMaxPoints() : 0,
                    gs.points()
            );
        }).toList();
        try {
            return objectMapper.writeValueAsString(rows);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сохранить снимок критериев", e);
        }
    }

    private void recordReviewHistory(LessonSubmissionEntity submission, Account reviewer, String criteriaSnapshotJson) {
        Account reviewerRef = accountRepository.getReferenceById(reviewer.getId());
        SubmissionReviewHistoryEntity row = SubmissionReviewHistoryEntity.builder()
                .submission(submission)
                .reviewer(reviewerRef)
                .entryKind(SubmissionReviewHistoryKind.MODERATOR)
                .statusAfter(submission.getStatus())
                .score(submission.getScore())
                .reviewerComment(submission.getReviewerComment())
                .criteriaSnapshotJson(criteriaSnapshotJson)
                .build();
        submissionReviewHistoryRepository.save(row);
    }

    private void assertDeadlineAllowsSubmission(CourseLessonEntity lesson) {
        LocalDateTime deadline = lesson.getSubmissionDeadline();
        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalStateException("Срок сдачи работы по этому уроку истёк");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Валидация
    // ──────────────────────────────────────────────────────────────────────────

    private void validateSubmissionContent(SubmissionType requiredType, String text, MultipartFile file) {
        boolean hasText = text != null && !text.isBlank();
        boolean hasFile = file != null && !file.isEmpty();

        if (requiredType == SubmissionType.TEXT) {
            if (!hasText) {
                throw new IllegalArgumentException("Для этого задания требуется текстовый ответ");
            }
        } else if (requiredType == SubmissionType.FILE) {
            if (!hasFile) {
                throw new IllegalArgumentException("Для этого задания требуется загрузить файл");
            }
        } else if (requiredType == SubmissionType.TEXT_AND_FILE) {
            if (!hasText || !hasFile) {
                throw new IllegalArgumentException("Для этого задания требуется и текст, и файл");
            }
        }
    }
}