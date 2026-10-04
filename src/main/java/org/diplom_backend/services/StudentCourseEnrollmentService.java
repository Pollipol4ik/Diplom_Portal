package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.StudentCourseEnrollment;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseRepository;
import org.diplom_backend.repositories.StudentCourseEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentCourseEnrollmentService {

    private final StudentCourseEnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public StudentCourseEnrollment enroll(Long studentId, Long courseId, Account assignedBy) {
        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new EntityModelNotFoundException("Ученик", "id", studentId.toString()));
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId)) {
            log.warn("Ученик {} уже назначен на курс {}", studentId, courseId);
            return null;
        }

        StudentCourseEnrollment enrollment = StudentCourseEnrollment.builder()
                .student(student)
                .course(course)
                .assignedAt(LocalDateTime.now())
                .assignedBy(assignedBy)
                .build();

        StudentCourseEnrollment saved = enrollmentRepository.save(enrollment);
        log.info("Ученик {} принудительно назначен на курс {}", student.getNickname(), course.getName());
        return saved;
    }

    @Transactional
    public void unenroll(Long studentId, Long courseId) {
        enrollmentRepository.deleteByStudentIdAndCourseId(studentId, courseId);
        log.info("Ученик {} откреплён от курса {}", studentId, courseId);
    }

    @Transactional(readOnly = true)
    public List<StudentCourseEnrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findAllByStudent_Id(studentId);
    }

    @Transactional(readOnly = true)
    public List<StudentCourseEnrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findAllByCourse_Id(courseId);
    }

    @Transactional(readOnly = true)
    public Set<Long> getForcedCourseIds(Long studentId) {
        List<Long> courseIds = enrollmentRepository.findCourseIdsByStudentId(studentId);
        return courseIds.stream().collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Long> getStudentIdsForcedToCourse(Long courseId) {
        List<Long> studentIds = enrollmentRepository.findStudentIdsByCourseId(courseId);
        return studentIds.stream().collect(Collectors.toSet());
    }

    @Transactional
    public void clearAllEnrollments(Long studentId) {
        enrollmentRepository.deleteAllByStudentId(studentId);
        log.info("Очищены все принудительные назначения для ученика {}", studentId);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudent_IdAndCourse_Id(studentId, courseId);
    }
}