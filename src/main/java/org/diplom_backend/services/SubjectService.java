package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.SubjectAlreadyExistsException;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.repositories.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DirectionService directionService;

    // ── Чтение ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<SubjectEntity> findAllByDirectionId(Integer pageNumber, Integer pageSize, Long directionId) {
        directionService.getDirectionById(directionId);
        return subjectRepository.findByDirection_Id(
                directionId,
                PageRequest.of(pageNumber, pageSize, Sort.by("name"))
        );
    }

    @Transactional(readOnly = true)
    public List<SubjectEntity> findAll() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SubjectEntity getSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Предмета", "id", Long.toString(id)));
    }

    // ── Запись ───────────────────────────────────────────────────────────────

    @Transactional
    public SubjectEntity createSubject(SubjectEntity subject) throws SubjectAlreadyExistsException {
        Long directionId = subject.getDirection().getId();
        var direction = directionService.getDirectionById(directionId);

        if (subjectRepository.existsSubjectByNameAndDirection_Id(subject.getName(), directionId)) {
            throw new SubjectAlreadyExistsException(subject.getName());
        }

        subject.setDirection(direction);
        return subjectRepository.save(subject);
    }

    @Transactional
    public SubjectEntity updateSubject(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название предмета не может быть пустым");
        }
        SubjectEntity subject = getSubject(id);
        subject.setName(name.trim().toUpperCase());
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        SubjectEntity subject = getSubject(id);
        subjectRepository.delete(subject);
    }
}