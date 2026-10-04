package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.CreateCourseLessonRequestDto;
import org.diplom_backend.dto.requests.CreateCourseRequestDto;
import org.diplom_backend.dto.requests.CreateGradingCriterionRequestDto;
import org.diplom_backend.dto.requests.CreateSubmissionReviewReplyRequestDto;
import org.diplom_backend.dto.requests.GradeSubmissionRequestDto;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.requests.ReviewHearingRequestDto;
import org.diplom_backend.dto.requests.ReviewSubmissionRequestDto;
import org.diplom_backend.dto.requests.UpdateCourseLaggingFlagRequestDto;
import org.diplom_backend.dto.responses.CourseLessonResponseDto;
import org.diplom_backend.dto.responses.CourseModeratorResponseDto;
import org.diplom_backend.dto.responses.CourseResponseDto;
import org.diplom_backend.dto.responses.CourseShortResponseDto;
import org.diplom_backend.dto.responses.CourseSummaryResponseDto;
import org.diplom_backend.dto.responses.CriterionGradeResponseDto;
import org.diplom_backend.dto.responses.GradingCriterionResponseDto;
import org.diplom_backend.dto.responses.HearingSubmissionResponseDto;
import org.diplom_backend.dto.responses.LessonSubmissionResponseDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.SchoolResponseDto;
import org.diplom_backend.dto.responses.SubmissionReviewHistoryResponseDto;
import org.diplom_backend.mappers.CourseLessonMapper;
import org.diplom_backend.mappers.CourseMapper;
import org.diplom_backend.mappers.CourseModeratorMapper;
import org.diplom_backend.mappers.HearingSubmissionMapper;
import org.diplom_backend.mappers.LessonSubmissionMapper;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.CourseLessonEntity;
import org.diplom_backend.model.HearingReview;
import org.diplom_backend.model.HearingSubmission;
import org.diplom_backend.model.LessonSubmissionEntity;
import org.diplom_backend.model.SubmissionCriterionGrade;
import org.diplom_backend.repositories.CourseRepository;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.security.annotations.IsUser;
import org.diplom_backend.services.CourseModeratorService;
import org.diplom_backend.services.CourseService;
import org.diplom_backend.services.HearingService;
import org.diplom_backend.services.StudentCourseEnrollmentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Курсы проектной деятельности", description = "Управление курсами, уроками и сдачей работ")
@RequestMapping("/v1/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseModeratorService courseModeratorService;
    private final HearingService hearingService;
    private final CourseMapper courseMapper;
    private final CourseLessonMapper lessonMapper;
    private final CourseModeratorMapper courseModeratorMapper;
    private final LessonSubmissionMapper submissionMapper;
    private final HearingSubmissionMapper hearingSubmissionMapper;
    private final PageMapper pageMapper;
    private final StudentCourseEnrollmentService enrollmentService;
    private final CourseRepository courseRepository;

    // ── Курсы ─────────────────────────────────────────────────────────────────

    @PostMapping
    @IsAdmin
    @Operation(summary = "Создать курс проектной деятельности (опционально с привязкой к школе)")
    public CourseResponseDto createCourse(@RequestBody @Valid CreateCourseRequestDto request) {
        return courseMapper.toResponseDto(
                courseService.createCourse(
                        request.name(),
                        request.description(),
                        request.schoolId(),
                        request.isIntroduction(),
                        request.forLaggingStudents()
                ));
    }

    @DeleteMapping("/{courseId}")
    @IsAdmin
    @Operation(summary = "Удалить курс")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Список всех активных курсов")
    public PageResponseDto<CourseShortResponseDto> getAllCourses(
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.getAllActiveCourses(pageDto.getPageNumber(), pageDto.getPageSize()),
                courseMapper::toShortResponseDto);
    }

    @GetMapping("/all")
    @IsAdmin
    @Operation(summary = "Список всех курсов (для админки)")
    public PageResponseDto<CourseShortResponseDto> getAllCoursesAdmin(
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.getAllCourses(pageDto.getPageNumber(), pageDto.getPageSize()),
                courseMapper::toShortResponseDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск курсов по названию")
    public PageResponseDto<CourseShortResponseDto> searchCourses(
            @RequestParam String query,
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.searchCourses(query, pageDto.getPageNumber(), pageDto.getPageSize()),
                courseMapper::toShortResponseDto);
    }

    @GetMapping("/my")
    @IsUser
    @Operation(summary = "Курсы моей школы")
    public List<CourseShortResponseDto> getMyCourses(@AuthenticationPrincipal Account account) {
        return courseService.getMySchoolCourses(account).stream()
                .map(courseMapper::toShortResponseDto)
                .toList();
    }

    @GetMapping("/my-moderated")
    @IsModerator
    @Operation(summary = "Курсы, на которые назначен текущий модератор")
    public List<CourseModeratorResponseDto> getMyModeratedCourses(@AuthenticationPrincipal Account account) {
        return courseModeratorMapper.toResponseDtoList(
                courseModeratorService.getCoursesByModerator(account.getId()));
    }

    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Курсы конкретной школы")
    public PageResponseDto<CourseShortResponseDto> getCoursesBySchool(
            @PathVariable Long schoolId,
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.getCoursesBySchool(schoolId, pageDto.getPageNumber(), pageDto.getPageSize()),
                courseMapper::toShortResponseDto);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Получить курс с уроками по ID")
    public CourseResponseDto getCourse(@PathVariable Long courseId) {
        return courseMapper.toResponseDto(courseService.getCourse(courseId));
    }

    @PatchMapping("/{courseId}/toggle-active")
    @IsAdmin
    @Operation(summary = "Включить/выключить курс")
    public CourseResponseDto toggleCourseActive(@PathVariable Long courseId) {
        return courseMapper.toResponseDto(courseService.toggleCourseActive(courseId));
    }

    @PatchMapping("/{courseId}/for-lagging-students")
    @IsAdmin
    @Operation(summary = "Курс для отстающих: автоматическое создание групп для учеников с флагом is_lagging по школам курса")
    public CourseResponseDto setForLaggingStudents(
            @PathVariable Long courseId,
            @RequestBody @Valid UpdateCourseLaggingFlagRequestDto request) {
        return courseMapper.toResponseDto(
                courseService.setForLaggingStudents(courseId, Boolean.TRUE.equals(request.forLaggingStudents())));
    }

    @PatchMapping("/{courseId}/is-introduction")
    @IsAdmin
    @Operation(summary = "Пометить курс как вводный (виден всем ученикам школы)")
    public CourseResponseDto setIsIntroduction(
            @PathVariable Long courseId,
            @RequestBody java.util.Map<String, Boolean> body) {
        boolean value = Boolean.TRUE.equals(body.get("is_introduction"));
        return courseMapper.toResponseDto(courseService.setIsIntroduction(courseId, value));
    }

    // ── Школы курса ──────────────────────────────────────────────────────────

    @PostMapping("/{courseId}/schools/{schoolId}")
    @IsAdmin
    @Operation(summary = "Привязать школу к курсу")
    public CourseResponseDto addSchoolToCourse(@PathVariable Long courseId,
                                               @PathVariable Long schoolId) {
        return courseMapper.toResponseDto(courseService.addSchoolToCourse(courseId, schoolId));
    }

    @DeleteMapping("/{courseId}/schools/{schoolId}")
    @IsAdmin
    @Operation(summary = "Открепить школу от курса")
    public CourseResponseDto removeSchoolFromCourse(@PathVariable Long courseId,
                                                    @PathVariable Long schoolId) {
        return courseMapper.toResponseDto(courseService.removeSchoolFromCourse(courseId, schoolId));
    }

    @GetMapping("/{courseId}/schools")
    @Operation(summary = "Список школ, привязанных к курсу")
    public List<SchoolResponseDto> getCourseSchools(@PathVariable Long courseId) {
        CourseEntity course = courseService.getCourse(courseId);
        return course.getSchools().stream()
                .map(s -> new SchoolResponseDto(s.getId(), s.getName(), s.getTopicDeadline()))
                .toList();
    }

    @GetMapping("/{courseId}/summary")
    @IsModerator
    @Operation(summary = "Сводная таблица по курсу: ученики и их баллы по каждому уроку")
    public CourseSummaryResponseDto getCourseSummary(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        return courseService.getCourseSummary(courseId);
    }

    // ── Все работы по курсу (админ / модератор) ──────────────────────────────

    @GetMapping("/{courseId}/submissions")
    @IsModerator
    @Operation(summary = "Все работы учеников по курсу (с фильтром по статусу)")
    public PageResponseDto<LessonSubmissionResponseDto> getSubmissionsByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) String status,
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.getSubmissionsByCourse(courseId, status,
                        pageDto.getPageNumber(), pageDto.getPageSize()),
                sub -> submissionMapper.toResponseDto(
                        sub,
                        courseService.getLessonMaxScoreConsideringCriteria(sub.getLesson().getId()),
                        courseService.getCriterionGrades(sub.getId())
                ));
    }

    // ── Уроки ─────────────────────────────────────────────────────────────────

    @PostMapping("/{courseId}/lessons")
    @IsModerator
    @Operation(summary = "Добавить урок (тему) в курс")
    public CourseLessonResponseDto addLesson(
            @PathVariable Long courseId,
            @RequestBody @Valid CreateCourseLessonRequestDto request,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        return lessonMapper.toResponseDto(
                courseService.addLesson(courseId, request.title(), request.lectureContent(),
                        request.practiceDescription(), request.submissionType(), request.orderNumber(),
                        request.videoUrl(), request.maxScore(), request.submissionDeadline(),
                        request.category(), request.hearingStage(), request.hearingOpenForStudents()));
    }

    @PutMapping("/{courseId}/lessons/{lessonId}")
    @IsModerator
    @Operation(summary = "Обновить содержание урока")
    public CourseLessonResponseDto updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody @Valid CreateCourseLessonRequestDto request,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        return lessonMapper.toResponseDto(
                courseService.updateLesson(courseId, lessonId, request.title(),
                        request.lectureContent(), request.practiceDescription(), request.submissionType(),
                        request.videoUrl(), request.maxScore(), request.submissionDeadline(),
                        request.category(), request.hearingStage(), request.hearingOpenForStudents()));
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    @IsModerator
    @Operation(summary = "Удалить урок")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        courseService.deleteLesson(courseId, lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/{courseId}/lessons/{lessonId}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IsModerator
    @Operation(summary = "Загрузить файл лекции (PDF, документ, презентация)")
    public CourseLessonResponseDto uploadLectureFile(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        return lessonMapper.toResponseDto(courseService.uploadLectureFile(courseId, lessonId, file));
    }

    @GetMapping("/{courseId}/lessons")
    @Operation(summary = "Список уроков курса")
    public List<CourseLessonResponseDto> getLessons(@PathVariable Long courseId) {
        return courseService.getLessonsByCourse(courseId).stream()
                .map(lessonMapper::toResponseDto)
                .toList();
    }

    // ── Сдача работ (ученик) ──────────────────────────────────────────────────

    @PostMapping(value = "/lessons/{lessonId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IsUser
    @Operation(summary = "Сдать практическое задание")
    public LessonSubmissionResponseDto submitWork(
            @PathVariable Long lessonId,
            @RequestParam(value = "text_content", required = false) String textContent,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Account account) {
        return submissionMapper.toResponseDto(
                courseService.submitWork(lessonId, textContent, file, account));
    }

    @PutMapping(value = "/lessons/{lessonId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IsUser
    @Operation(summary = "Обновить сданную работу")
    public LessonSubmissionResponseDto updateSubmission(
            @PathVariable Long lessonId,
            @RequestParam(value = "text_content", required = false) String textContent,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Account account) {
        return submissionMapper.toResponseDto(
                courseService.updateSubmission(lessonId, textContent, file, account));
    }

    @GetMapping("/lessons/{lessonId}/my-submission")
    @IsUser
    @Operation(summary = "Получить свою работу по уроку")
    public LessonSubmissionResponseDto getMySubmission(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Account account) {
        LessonSubmissionEntity submission = courseService.getMySubmission(lessonId, account.getId());
        if (submission == null) return null;
        List<SubmissionCriterionGrade> grades = courseService.getCriterionGrades(submission.getId());
        return submissionMapper.toResponseDto(
                submission,
                courseService.getLessonMaxScoreConsideringCriteria(submission.getLesson().getId()),
                grades
        );
    }

    @GetMapping("/{courseId}/my-submissions")
    @IsUser
    @Operation(summary = "Получить все свои работы по курсу")
    public List<LessonSubmissionResponseDto> getMySubmissionsForCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Account account) {
        return courseService.getMySubmissionsForCourse(courseId, account.getId()).stream()
                .map(sub -> {
                    List<SubmissionCriterionGrade> grades = courseService.getCriterionGrades(sub.getId());
                    return submissionMapper.toResponseDto(
                            sub,
                            courseService.getLessonMaxScoreConsideringCriteria(sub.getLesson().getId()),
                            grades
                    );
                })
                .toList();
    }

    // ── Проверка работ (модератор) ────────────────────────────────────────────

    @GetMapping("/lessons/{lessonId}/submissions")
    @IsModerator
    @Operation(summary = "Все работы учеников по уроку")
    public PageResponseDto<LessonSubmissionResponseDto> getSubmissionsByLesson(
            @PathVariable Long lessonId,
            @ParameterObject @Valid PageRequestDto pageDto) {
        return pageMapper.toPageResponseDto(
                courseService.getSubmissionsByLesson(lessonId,
                        pageDto.getPageNumber(), pageDto.getPageSize()),
                sub -> submissionMapper.toResponseDto(
                        sub,
                        courseService.getLessonMaxScoreConsideringCriteria(sub.getLesson().getId()),
                        courseService.getCriterionGrades(sub.getId())
                ));
    }

    @PostMapping("/submissions/{submissionId}/review")
    @IsModerator
    @Operation(summary = "Проверить работу ученика (простая проверка)")
    public LessonSubmissionResponseDto reviewSubmission(
            @PathVariable Long submissionId,
            @RequestBody @Valid ReviewSubmissionRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        LessonSubmissionEntity reviewed = courseService.reviewSubmission(
                submissionId, request.status(), request.reviewerComment(), request.score(), moderator);
        List<SubmissionCriterionGrade> grades = courseService.getCriterionGrades(submissionId);
        return submissionMapper.toResponseDto(
                reviewed,
                courseService.getLessonMaxScoreConsideringCriteria(reviewed.getLesson().getId()),
                grades
        );
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @IsModerator
    @Operation(summary = "Оценить работу по критериям")
    public LessonSubmissionResponseDto gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody @Valid GradeSubmissionRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        LessonSubmissionEntity graded = courseService.gradeSubmissionByCriteria(submissionId, request, moderator);
        List<SubmissionCriterionGrade> grades = courseService.getCriterionGrades(submissionId);
        return submissionMapper.toResponseDto(
                graded,
                courseService.getLessonMaxScoreConsideringCriteria(graded.getLesson().getId()),
                grades
        );
    }

    // ── Критерии оценивания (привязаны к уроку) ─────────────────────────────

    @GetMapping("/lessons/{lessonId}/criteria")
    @Operation(summary = "Получить критерии оценивания урока")
    public List<GradingCriterionResponseDto> getLessonCriteria(@PathVariable Long lessonId) {
        return courseService.getCriteriaByLesson(lessonId).stream()
                .map(c -> new GradingCriterionResponseDto(
                        c.getId(), c.getOrderNumber(), c.getName(),
                        c.getDescription(), c.getMaxPoints()))
                .toList();
    }

    @PutMapping("/lessons/{lessonId}/criteria")
    @IsModerator
    @Operation(summary = "Установить критерии оценивания для урока")
    public List<GradingCriterionResponseDto> setLessonCriteria(
            @PathVariable Long lessonId,
            @RequestBody @Valid List<CreateGradingCriterionRequestDto> criteria,
            @AuthenticationPrincipal Account account) {
        CourseLessonEntity lesson = courseService.getLesson(lessonId);
        courseModeratorService.verifyModeratorAccessToCourse(account, lesson.getCourse().getId());
        return courseService.setLessonCriteria(lessonId, criteria).stream()
                .map(c -> new GradingCriterionResponseDto(
                        c.getId(), c.getOrderNumber(), c.getName(),
                        c.getDescription(), c.getMaxPoints()))
                .toList();
    }

    @PostMapping("/submissions/{submissionId}/review-reply")
    @IsUser
    @Operation(summary = "Ответ ученика на комментарий/оценку (запись в истории проверки)")
    public SubmissionReviewHistoryResponseDto addSubmissionReviewReply(
            @PathVariable Long submissionId,
            @RequestBody @Valid CreateSubmissionReviewReplyRequestDto request,
            @AuthenticationPrincipal Account account) {
        return courseService.addStudentSubmissionReply(submissionId, request.comment(), account);
    }

    @PostMapping("/submissions/{submissionId}/moderator-comment")
    @IsModerator
    @Operation(summary = "Добавить комментарий модератора без изменения оценки (для уже проверенных работ)")
    public SubmissionReviewHistoryResponseDto addModeratorComment(
            @PathVariable Long submissionId,
            @RequestBody @Valid CreateSubmissionReviewReplyRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        return courseService.addModeratorComment(submissionId, request.comment(), moderator);
    }

    @GetMapping("/submissions/{submissionId}/review-history")
    @IsUser
    @Operation(summary = "История проверок работы (комментарии и оценки)")
    public List<SubmissionReviewHistoryResponseDto> getSubmissionReviewHistory(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal Account account) {
        return courseService.getSubmissionReviewHistory(submissionId, account);
    }

    @GetMapping("/submissions/{submissionId}/grades")
    @Operation(summary = "Получить оценки по критериям для работы")
    public List<CriterionGradeResponseDto> getSubmissionGrades(@PathVariable Long submissionId) {
        return courseService.getCriterionGrades(submissionId).stream()
                .map(g -> new CriterionGradeResponseDto(
                        g.getId(), g.getCriterion().getId(),
                        g.getCriterion().getName(), g.getCriterion().getMaxPoints(),
                        g.getPoints()))
                .toList();
    }

    // ── Слушания (hearing) ────────────────────────────────────────────────────

    @PostMapping(value = "/lessons/{lessonId}/hearing-submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IsUser
    @Operation(summary = "Отправить работу на слушание (от лица владельца группы)")
    public HearingSubmissionResponseDto submitHearing(
            @PathVariable Long lessonId,
            @RequestParam("group_id") Long groupId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Account account) {
        return hearingSubmissionMapper.toResponseDto(
                hearingService.submitHearing(lessonId, groupId, file, account));
    }

    @PutMapping(value = "/lessons/{lessonId}/hearing-submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IsUser
    @Operation(summary = "Повторно отправить работу на слушание")
    public HearingSubmissionResponseDto resubmitHearing(
            @PathVariable Long lessonId,
            @RequestParam("group_id") Long groupId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Account account) {
        return hearingSubmissionMapper.toResponseDto(
                hearingService.resubmitHearing(lessonId, groupId, file, account));
    }

    @GetMapping("/lessons/{lessonId}/hearing-submissions")
    @IsModerator
    @Operation(summary = "Все работы групп по слушанию")
    public List<HearingSubmissionResponseDto> getHearingSubmissions(@PathVariable Long lessonId) {
        return hearingService.getSubmissionsByLesson(lessonId).stream()
                .map(hearingSubmissionMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/lessons/{lessonId}/my-hearing-submission")
    @IsUser
    @Operation(summary = "Получить работу моей группы по слушанию")
    public HearingSubmissionResponseDto getMyHearingSubmission(
            @PathVariable Long lessonId,
            @RequestParam("group_id") Long groupId) {
        HearingSubmission sub = hearingService.getSubmissionByLessonAndGroup(lessonId, groupId);
        return sub != null ? hearingSubmissionMapper.toResponseDto(sub) : null;
    }

    @PostMapping("/hearing-submissions/{submissionId}/review")
    @IsModerator
    @Operation(summary = "Проверить работу на слушании")
    public HearingSubmissionResponseDto reviewHearing(
            @PathVariable Long submissionId,
            @RequestBody @Valid ReviewHearingRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        HearingReview review = hearingService.reviewHearing(
                submissionId, request.comment(), request.grade(), request.newStatus(), moderator);
        return hearingSubmissionMapper.toResponseDto(review.getSubmission());
    }

    @PostMapping("/hearing-submissions/{submissionId}/comment")
    @IsUser
    @Operation(summary = "Комментарий ученика по слушанию (переписка)")
    public HearingSubmissionResponseDto addStudentHearingComment(
            @PathVariable Long submissionId,
            @RequestBody @Valid CreateSubmissionReviewReplyRequestDto request,
            @AuthenticationPrincipal Account account) {
        hearingService.addStudentComment(submissionId, request.comment(), account);
        HearingSubmission sub = hearingService.getSubmissionByIdWithReviews(submissionId);
        return hearingSubmissionMapper.toResponseDto(sub);
    }

    @GetMapping("/moderated/{courseId}")
    @IsModerator
    @Operation(summary = "Получить курс для модератора (с полной информацией)")
    public CourseResponseDto getModeratedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        return courseMapper.toResponseDto(courseService.getCourse(courseId));
    }
    @GetMapping("/for-lagging-transfer")
    @IsModerator
    @Operation(summary = "Курсы для перевода отстающих учеников (только for_lagging_students=true)")
    public List<CourseShortResponseDto> getCoursesForLaggingTransfer(@AuthenticationPrincipal Account account) {
        List<Long> accessibleSchoolIds = courseModeratorService.getAccessibleSchoolIds(account);
        if (accessibleSchoolIds.isEmpty()) {
            return List.of();
        }

        List<CourseEntity> courses = courseRepository.findByForLaggingStudentsTrueAndIsActiveTrueAndSchools_IdIn(accessibleSchoolIds);
        return courses.stream()
                .map(courseMapper::toShortResponseDto)
                .toList();
    }
}