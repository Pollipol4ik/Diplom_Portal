package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectModerator;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.SubjectModeratorRepository;
import org.diplom_backend.repositories.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminModeratorService {
    private final SubjectModeratorRepository moderatorRepository;
    private final AccountRepository accountRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public SubjectModerator assignModerator(Long accountId, Long subjectId) throws EntityModelNotFoundException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityModelNotFoundException("Аккаунт", "id", accountId.toString()));

        if (!account.getRole().getName().equals(Role.ROLE_MODERATOR)) {
            throw new IllegalStateException("Пользователь не имеет роли модератора. Сначала смените его глобальную роль.");
        }

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityModelNotFoundException("Предмет", "id", subjectId.toString()));

        if (moderatorRepository.existsByAccount_IdAndSubject_Id(accountId, subjectId)) {
            throw new IllegalStateException("Этот модератор уже назначен на данный предмет");
        }

        SubjectModerator link = SubjectModerator.builder()
                .account(account)
                .subject(subject)
                .assignedAt(LocalDateTime.now())
                .build();

        return moderatorRepository.save(link);
    }

    @Transactional(readOnly = true)
    public List<SubjectModerator> getModeratorsBySubject(Long subjectId) {
        return moderatorRepository.findAllBySubject_Id(subjectId);
    }

    @Transactional
    public void removeModerator(Long accountId, Long subjectId) {
        if (!moderatorRepository.existsByAccount_IdAndSubject_Id(accountId, subjectId)) {
            throw new EntityModelNotFoundException("Связь модератора с предметом", "ids", accountId + "/" + subjectId);
        }
        moderatorRepository.deleteByAccount_IdAndSubject_Id(accountId, subjectId);
    }

    @Transactional(readOnly = true)
    public List<SubjectModerator> getMySubjects(Account account) {
        return moderatorRepository.findAllByAccount_Id(account.getId());
    }

}