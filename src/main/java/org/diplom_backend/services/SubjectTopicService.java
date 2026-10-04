package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.SubjectTopicAlreadyExistsException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.repositories.SubjectTopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectTopicService {

    private final SubjectTopicRepository subjectTopicRepository;
    private final SubjectService subjectService;
    private final AccessControlService accessControlService;

    // ── Чтение ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public SubjectTopicEntity getSubjectTopic(Long id) {
        return subjectTopicRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Топика", "id", id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<SubjectTopicEntity> findAllBySubjectId(Integer pageNumber, Integer pageSize, Long subjectId) {
        return subjectTopicRepository.findBySubject_Id(
                subjectId,
                PageRequest.of(pageNumber, pageSize, Sort.by("id"))
        );
    }

    @Transactional(readOnly = true)
    public SubjectTopicEntity getSubjectTopicWithRelations(Long id) {
        return subjectTopicRepository.findByIdWithSubjectAndCourse(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Топика", "id", Long.toString(id)));
    }

    // ── Запись ───────────────────────────────────────────────────────────────

    @Transactional
    public SubjectTopicEntity createSubjectTopic(SubjectTopicEntity subjectTopic, Account currentAccount) {
        accessControlService.verifyModeratorAccess(currentAccount, subjectTopic.getSubject().getId());

        SubjectEntity subject = subjectService.getSubject(subjectTopic.getSubject().getId());
        subjectTopic.setSubject(subject);

        if (subjectTopicRepository.existsBySubjectAndName(subject, subjectTopic.getName())) {
            throw new SubjectTopicAlreadyExistsException();
        }

        return subjectTopicRepository.save(subjectTopic);
    }

    /**
     * Обновление названия темы. Доступно администратору и модератору предмета.
     */
    @Transactional
    public SubjectTopicEntity updateSubjectTopic(Long id, String name, Account currentAccount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название темы не может быть пустым");
        }
        SubjectTopicEntity topic = getSubjectTopic(id);
        accessControlService.verifyModeratorAccess(currentAccount, topic.getSubject().getId());
        topic.setName(name.trim());
        return subjectTopicRepository.save(topic);
    }

    @Transactional
    public void deleteSubjectTopic(Long id, Account currentAccount) {
        SubjectTopicEntity topic = getSubjectTopic(id);
        accessControlService.verifyModeratorAccess(currentAccount, topic.getSubject().getId());
        subjectTopicRepository.delete(topic);
    }
}