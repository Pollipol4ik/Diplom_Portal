package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.DirectionEntity;
import org.diplom_backend.repositories.DirectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectionService {

    private final DirectionRepository directionRepository;

    // ── Чтение ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DirectionEntity> findAll() {
        return directionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DirectionEntity getDirectionById(Long id) {
        return directionRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Направления", "id", id.toString()));
    }

    // ── Запись ───────────────────────────────────────────────────────────────

    @Transactional
    public DirectionEntity createDirection(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название направления не может быть пустым");
        }
        DirectionEntity direction = new DirectionEntity();
        direction.setName(name.trim());
        return directionRepository.save(direction);
    }

    @Transactional
    public DirectionEntity updateDirection(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название направления не может быть пустым");
        }
        DirectionEntity direction = getDirectionById(id);
        direction.setName(name.trim());
        return directionRepository.save(direction);
    }

    @Transactional
    public void deleteDirection(Long id) {
        DirectionEntity direction = getDirectionById(id);
        directionRepository.delete(direction);
    }
}