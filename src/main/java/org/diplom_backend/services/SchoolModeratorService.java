package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.School;
import org.diplom_backend.model.SchoolModerator;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.CourseModeratorRepository;
import org.diplom_backend.repositories.SchoolModeratorRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Управление закреплением модераторов за школами.
 * Только администратор может назначать и снимать модераторов.
 */
@Service
@RequiredArgsConstructor
public class SchoolModeratorService {

    private final SchoolModeratorRepository schoolModeratorRepository;
    private final CourseModeratorRepository courseModeratorRepository;
    private final AccountRepository accountRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public SchoolModerator assignModerator(Long accountId, Long schoolId) throws EntityModelNotFoundException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Аккаунт", "id", accountId.toString()));

        if (!account.getRole().getName().equals(Role.ROLE_MODERATOR)) {
            throw new NotEnoughRightsException("Пользователь не имеет роли модератора. Сначала измените его роль.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));

        if (schoolModeratorRepository.existsByAccount_IdAndSchool_Id(accountId, schoolId)) {
            throw new IllegalStateException("Модератор уже закреплён за этой школой");
        }

        SchoolModerator link = SchoolModerator.builder()
                .account(account)
                .school(school)
                .assignedAt(LocalDateTime.now())
                .build();

        return schoolModeratorRepository.save(link);
    }

    @Transactional(readOnly = true)
    public List<SchoolModerator> getModeratorsBySchool(Long schoolId) {
        return schoolModeratorRepository.findAllBySchool_Id(schoolId);
    }

    @Transactional(readOnly = true)
    public List<SchoolModerator> getSchoolsByModerator(Long accountId) {
        return schoolModeratorRepository.findAllByAccount_Id(accountId);
    }

    @Transactional
    public void removeModerator(Long accountId, Long schoolId) {
        if (!schoolModeratorRepository.existsByAccount_IdAndSchool_Id(accountId, schoolId)) {
            throw new EntityModelNotFoundException("Назначение модератора", "ids", accountId + "/" + schoolId);
        }
        schoolModeratorRepository.deleteByAccount_IdAndSchool_Id(accountId, schoolId);
    }

    /**
     * Проверяет, закреплён ли данный модератор (или является ли он админом)
     * за школой, к которой принадлежит проект.
     */
    public void verifyModeratorAccessToSchool(Account account, Long schoolId) {
        if (account.getRole().getName().equals(Role.ROLE_ADMIN)) {
            return;
        }
        boolean hasAccess = schoolModeratorRepository.existsByAccount_IdAndSchool_Id(account.getId(), schoolId)
                || courseModeratorRepository.existsModeratorLinkedToSchool(account.getId(), schoolId);
        if (!hasAccess) {
            throw new NotEnoughRightsException("У вас нет прав для работы с этой школой");
        }
    }
}
