package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.repositories.SubjectModeratorRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {
    private final SubjectModeratorRepository moderatorRepository;

    /**
     * Проверяет, имеет ли пользователь право модерировать данный предмет.
     * Если это админ — доступ разрешен всегда.
     * Если модератор — проверяем связь в таблице subject_moderator.
     */
    public void verifyModeratorAccess(Account account, Long subjectId) {
        if (account.getRole().getName().equals(Role.ROLE_ADMIN)) {
            return;
        }

        boolean hasLink = moderatorRepository.existsByAccount_IdAndSubject_Id(account.getId(), subjectId);

        if (!account.getRole().getName().equals(Role.ROLE_MODERATOR) || !hasLink) {
            throw new NotEnoughRightsException();
        }
    }
}