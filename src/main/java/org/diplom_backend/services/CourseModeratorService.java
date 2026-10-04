package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.*;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseModeratorRepository;
import org.diplom_backend.repositories.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseModeratorService {

    private final CourseModeratorRepository courseModeratorRepository;
    private final AccountRepository accountRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public CourseModerator assignModerator(Long accountId, Long courseId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Аккаунт", "id", accountId.toString()));

        if (!account.getRole().getName().equals(Role.ROLE_MODERATOR)) {
            throw new NotEnoughRightsException("Пользователь не имеет роли модератора");
        }

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityModelNotFoundException("Курс", "id", courseId.toString()));

        if (courseModeratorRepository.existsByAccount_IdAndCourse_Id(accountId, courseId)) {
            throw new IllegalStateException("Модератор уже назначен на этот курс");
        }

        CourseModerator link = CourseModerator.builder()
                .account(account)
                .course(course)
                .assignedAt(LocalDateTime.now())
                .build();

        return courseModeratorRepository.save(link);
    }

    @Transactional(readOnly = true)
    public List<CourseModerator> getModeratorsByCourse(Long courseId) {
        return courseModeratorRepository.findAllByCourse_Id(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseModerator> getCoursesByModerator(Long accountId) {
        return courseModeratorRepository.findAllByAccount_Id(accountId);
    }

    @Transactional
    public void removeModerator(Long accountId, Long courseId) {
        if (!courseModeratorRepository.existsByAccount_IdAndCourse_Id(accountId, courseId)) {
            throw new EntityModelNotFoundException("Назначение модератора", "ids", accountId + "/" + courseId);
        }
        courseModeratorRepository.deleteByAccount_IdAndCourse_Id(accountId, courseId);
    }

    public void verifyModeratorAccessToCourse(Account account, Long courseId) {
        if (account.getRole().getName().equals(Role.ROLE_ADMIN)) {
            return;
        }
        if (!courseModeratorRepository.existsByAccount_IdAndCourse_Id(account.getId(), courseId)) {
            throw new NotEnoughRightsException("У вас нет прав для работы с этим курсом");
        }
    }

    /**
     * Проверяет, имеет ли модератор доступ к школе через хотя бы один из своих курсов.
     * Заменяет SchoolModeratorService.verifyModeratorAccessToSchool — таблица school_moderator не нужна.
     */
    public void verifyModeratorAccessToSchool(Account account, Long schoolId) {
        if (account.getRole().getName().equals(Role.ROLE_ADMIN)) {
            return;
        }
        boolean hasAccess = courseModeratorRepository.existsModeratorLinkedToSchool(account.getId(), schoolId);
        if (!hasAccess) {
            throw new NotEnoughRightsException("У вас нет прав для работы с этой школой");
        }
    }

    /**
     * Возвращает список школ, доступных модератору через его курсы.
     * Заменяет связку SchoolModeratorRepository + CourseModeratorRepository в StudentRatingService.
     */
    public List<Long> getAccessibleSchoolIds(Account moderator) {
        return courseModeratorRepository.findDistinctSchoolIdsForModeratorCourses(moderator.getId());
    }
}