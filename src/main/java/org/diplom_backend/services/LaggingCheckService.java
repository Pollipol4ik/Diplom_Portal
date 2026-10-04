package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.CourseGroup;
import org.diplom_backend.model.CourseGroupMember;
import org.diplom_backend.model.CourseLessonEntity;
import org.diplom_backend.model.HearingStage;
import org.diplom_backend.model.LessonCategory;
import org.diplom_backend.model.School;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseGroupMemberRepository;
import org.diplom_backend.repositories.CourseGroupRepository;
import org.diplom_backend.repositories.CourseLessonRepository;
import org.diplom_backend.repositories.CourseRepository;
import org.diplom_backend.repositories.HearingSubmissionRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Логика определения отстающих:
 * <p>
 * Ученик считается отстающим, если он прикреплён к курсу (через школу),
 * в курсе есть этап слушания TOPIC_APPROVAL с заданным {@code submission_deadline},
 * дедлайн уже прошёл, а ученик НЕ загрузил работу по этому этапу
 * (нет HearingSubmission для его группы, или ученик вообще не в группе).
 * <p>
 * Если работа загружена — ученик НЕ отстающий, независимо от времени создания группы.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LaggingCheckService {

    private final SchoolRepository schoolRepository;
    private final AccountRepository accountRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final CourseGroupMemberRepository courseGroupMemberRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final CourseRepository courseRepository;
    private final CourseGroupService courseGroupService;
    private final HearingSubmissionRepository hearingSubmissionRepository;
    private final StudentCourseEnrollmentService studentCourseEnrollmentService;

    /**
     * Проверяет всех учеников и ставит/снимает флаг isLagging.
     * Вызывается по крону (LaggingCheckJob) и вручную из SchedulerController.
     */

    @Transactional
    public int checkAndMarkLagging() {
        LocalDateTime now = LocalDateTime.now();

        List<CourseLessonEntity> topicLessons = courseLessonRepository
                .findByCategoryAndHearingStageWithCourseSchools(LessonCategory.HEARING, HearingStage.TOPIC_APPROVAL);

        List<CourseLessonEntity> expiredTopicLessons = topicLessons.stream()
                .filter(l -> l.getSubmissionDeadline() != null && now.isAfter(l.getSubmissionDeadline()))
                .toList();

        int changed = 0;
        for (School school : schoolRepository.findAll()) {
            Long schoolId = school.getId();
            List<Account> students = accountRepository.findAllStudentsOfSchool(schoolId);
            if (students.isEmpty()) {
                continue;
            }

            for (Account student : students) {
                boolean shouldLag = false;

                if (!expiredTopicLessons.isEmpty()) {
                    for (CourseLessonEntity lesson : expiredTopicLessons) {
                        CourseEntity course = lesson.getCourse();

                        if (course.getSchools() != null && !course.getSchools().isEmpty()
                                && course.getSchools().stream().noneMatch(s -> s.getId().equals(schoolId))) {
                            continue;
                        }

                        if (!hasSubmittedTopicApproval(student.getId(), course.getId(), lesson.getId())) {
                            shouldLag = true;
                            break;
                        }
                    }
                }

                boolean wasLagging = Boolean.TRUE.equals(student.getIsLagging());

                if (wasLagging != shouldLag) {
                    student.setIsLagging(shouldLag);
                    accountRepository.save(student);
                    changed++;
                    log.debug("Ученик id={} ({}): is_lagging -> {}", student.getId(), student.getNickname(), shouldLag);

                    if (shouldLag) {
                        removeFromNonLaggingCourses(student);
                        removeFromForcedEnrollments(student);
                        log.info("Ученик '{}' помечен отстающим, удалён из целевых курсов и принудительных назначений", student.getNickname());
                    }
                }
            }
        }

        enrollAllLaggingStudentsIntoLaggingCourses();
        return changed;
    }

    /**
     * Удаляет принудительные назначения ученика со всех целевых курсов
     */
    private void removeFromForcedEnrollments(Account student) {
        Set<Long> forcedCourseIds = studentCourseEnrollmentService.getForcedCourseIds(student.getId());
        for (Long courseId : forcedCourseIds) {
            CourseEntity course = courseRepository.findById(courseId).orElse(null);
            if (course != null && !Boolean.TRUE.equals(course.getForLaggingStudents())) {
                studentCourseEnrollmentService.unenroll(student.getId(), courseId);
                log.info("Ученик '{}' откреплён от целевого курса '{}' (принудительное назначение удалено)",
                        student.getNickname(), course.getName());
            }
        }
    }

    /**
     * Для каждого активного курса «для отстающих» создаёт группу ученикам с флагом is_lagging из привязанных школ,
     * если они ещё не в этом курсе. Флаг отстающего не снимается — это отдельная поддержка.
     */
    @Transactional
    public void enrollAllLaggingStudentsIntoLaggingCourses() {
        for (CourseEntity c : courseRepository.findByForLaggingStudentsTrueAndIsActiveTrue()) {
            enrollLaggingStudentsForCourse(c.getId());
        }
    }

    @Transactional
    /**
     * Переводит отстающих учеников в lagging-курс:
     * удаляет их из обычных (не-lagging) групп.
     * НЕ создаёт группы автоматически — ученик сам выбирает тему и состав группы.
     */
    public void enrollLaggingStudentsForCourse(Long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        if (!Boolean.TRUE.equals(course.getForLaggingStudents()) || !Boolean.TRUE.equals(course.getIsActive())) {
            return;
        }

        Set<School> schools = course.getSchools();
        if (schools == null || schools.isEmpty()) {
            return;
        }

        for (School school : schools) {
            List<Account> lagging = accountRepository.findLaggingStudentsOfSchool(school.getId());
            for (Account student : lagging) {
                removeFromNonLaggingCourses(student);
                log.info("Отстающий ученик '{}' — удалён из обычных курсов, lagging-курс '{}' доступен",
                        student.getNickname(), course.getName());
            }
        }
    }

    /**
     * Удаляет ученика из всех целевых групп (не lagging, не introduction). Пустые группы удаляет.
     */
    private void removeFromNonLaggingCourses(Account student) {
        List<CourseGroupMember> memberships = courseGroupMemberRepository.findAllByAccount(student.getId());
        for (CourseGroupMember membership : memberships) {
            CourseGroup group = membership.getGroup();
            CourseEntity memberCourse = group.getCourse();
            if (Boolean.TRUE.equals(memberCourse.getForLaggingStudents())) continue;
            if (Boolean.TRUE.equals(memberCourse.getIsIntroduction())) continue;
            courseGroupMemberRepository.deleteByGroup_IdAndAccount_Id(group.getId(), student.getId());
            log.info("Ученик '{}' удалён из целевой группы '{}' (курс '{}')",
                    student.getNickname(), group.getTitle(), memberCourse.getName());
            if (courseGroupMemberRepository.findByGroup_Id(group.getId()).isEmpty()) {
                courseGroupRepository.delete(group);
            }
        }
    }

    /**
     * Проверяет, загрузил ли ученик работу по этапу TOPIC_APPROVAL.
     * Ученик НЕ отстающий если: (1) состоит в группе курса И (2) у группы есть HearingSubmission по уроку.
     */
    private boolean hasSubmittedTopicApproval(Long accountId, Long courseId, Long lessonId) {
        var membership = courseGroupMemberRepository.findByAccountAndCourse(accountId, courseId);
        if (membership.isEmpty()) {
            return false;
        }

        CourseGroup group = membership.get().getGroup();
        if (group == null) return false;

        var submission = hearingSubmissionRepository.findByLesson_IdAndGroup_Id(lessonId, group.getId());
        return submission.isPresent();
    }

    /**
     * Вызывается в реальном времени при создании/обновлении группы:
     * снимает флаг isLagging у переданных учеников.
     * Это нужно потому что isLagging = true по умолчанию,
     * а крон checkAndMarkLagging запускается не мгновенно.
     */
    @Transactional
    public void clearLaggingForStudents(List<Long> studentIds) {
        for (Long studentId : studentIds) {
            Account account = accountRepository.findById(studentId).orElse(null);
            if (account != null && Boolean.TRUE.equals(account.getIsLagging())) {
                account.setIsLagging(false);
                accountRepository.save(account);
                log.info("Снят флаг отстающего у ученика '{}' (при создании/подключении к группе)",
                        account.getNickname());
            }
        }
    }

    /**
     * Переводит ученика на целевой курс (создаёт принудительное назначение без группы)
     * Логика:
     * — Обычный ученик → целевой курс (не вводный, не для отстающих)
     * — Отстающий ученик → только курс для отстающих (forLaggingStudents=true)
     *   При переводе отстающего на lagging-курс флаг is_lagging НЕ снимается.
     */
    @Transactional
    public void transferToCourse(Long accountId, Long targetCourseId, Account moderator) {
        Account student = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", accountId.toString()));

        CourseEntity targetCourse = courseRepository.findById(targetCourseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", targetCourseId.toString()));

        boolean studentIsLagging = Boolean.TRUE.equals(student.getIsLagging());
        boolean courseIsLagging = Boolean.TRUE.equals(targetCourse.getForLaggingStudents());
        boolean courseIsIntroduction = Boolean.TRUE.equals(targetCourse.getIsIntroduction());

        if (courseIsIntroduction) {
            throw new IllegalStateException("Нельзя вручную переводить учеников во вводный курс");
        }
        if (studentIsLagging && !courseIsLagging) {
            throw new IllegalStateException(
                    "Отстающий ученик может быть переведён только в курс для отстающих. " +
                            "Сначала снимите флаг отстающего через кнопку «✕».");
        }
        if (!studentIsLagging && courseIsLagging) {
            throw new IllegalStateException(
                    "Обычный ученик не может быть переведён в курс для отстающих.");
        }

        if (studentCourseEnrollmentService.isEnrolled(accountId, targetCourseId)) {
            throw new IllegalStateException("Ученик уже назначен на этот курс");
        }

        courseGroupMemberRepository.findByAccountAndCourse(accountId, targetCourseId).ifPresent(existing -> {
            throw new IllegalStateException("Ученик уже состоит в группе курса «"
                    + existing.getGroup().getTitle() + "»");
        });

        Long schoolId = student.getSchool() != null ? student.getSchool().getId() : null;
        if (schoolId == null) {
            throw new IllegalStateException("У ученика не указана школа");
        }

        List<CourseGroupMember> existingMemberships = courseGroupMemberRepository.findAllByAccount(accountId);
        for (CourseGroupMember membership : existingMemberships) {
            CourseGroup group = membership.getGroup();
            CourseEntity memberCourse = group.getCourse();

            if (Boolean.TRUE.equals(memberCourse.getIsIntroduction())) continue;

            courseGroupMemberRepository.deleteByGroup_IdAndAccount_Id(group.getId(), accountId);
            log.info("Ученик '{}' удалён из группы '{}' (курс '{}')",
                    student.getNickname(), group.getTitle(), memberCourse.getName());

            if (courseGroupMemberRepository.findByGroup_Id(group.getId()).isEmpty()) {
                courseGroupRepository.delete(group);
            }
        }

        studentCourseEnrollmentService.enroll(accountId, targetCourseId, moderator);

        if (studentIsLagging && !courseIsLagging) {
            student.setIsLagging(false);
            accountRepository.save(student);
            log.info("Снят флаг отстающего у ученика '{}' после перевода на целевой курс '{}'",
                    student.getNickname(), targetCourse.getName());
        }

        log.info("Ученик '{}' принудительно назначен на курс '{}' (id={}, lagging={})",
                student.getNickname(), targetCourse.getName(), targetCourseId, courseIsLagging);
    }

    /**
     * Устаревший метод — оставлен для обратной совместимости.
     */
    @Deprecated
    @Transactional
    public void transferToLaggingCourse(Long accountId, Long targetCourseId, Account moderator) {
        transferToCourse(accountId, targetCourseId, moderator);
    }


    /**
     * Снимает флаг отстающего и возвращает ученика к обычным курсам:
     * - Удаляет из групп в lagging-курсах
     * - Устанавливает is_lagging = false
     * - Обычные курсы школы становятся видны через getMySchoolCourses
     */
    @Transactional
    public void unmarkLagging(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null || !Boolean.TRUE.equals(account.getIsLagging())) return;

        List<CourseGroupMember> memberships = courseGroupMemberRepository.findAllByAccount(accountId);
        for (CourseGroupMember membership : memberships) {
            CourseGroup group = membership.getGroup();
            if (!Boolean.TRUE.equals(group.getCourse().getForLaggingStudents())) continue;
            courseGroupMemberRepository.deleteByGroup_IdAndAccount_Id(group.getId(), accountId);
            log.info("Ученик '{}' удалён из lagging-группы '{}' при восстановлении статуса",
                    account.getNickname(), group.getTitle());
            if (courseGroupMemberRepository.findByGroup_Id(group.getId()).isEmpty()) {
                courseGroupRepository.delete(group);
            }
        }

        account.setIsLagging(false);
        accountRepository.save(account);
        log.info("Снят флаг отстающего у ученика '{}'. Теперь видит обычные курсы школы.", account.getNickname());
    }

    /**
     * Снять принудительное назначение ученика с курса
     */
    @Transactional
    public void unassignFromCourse(Long accountId, Long courseId) {
        Account student = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", accountId.toString()));

        if (!studentCourseEnrollmentService.isEnrolled(accountId, courseId)) {
            throw new IllegalStateException("Ученик не был назначен на этот курс");
        }

        studentCourseEnrollmentService.unenroll(accountId, courseId);
        log.info("Снято принудительное назначение ученика '{}' с курса id={}",
                student.getNickname(), courseId);
    }
}