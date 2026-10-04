package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.SchoolAlreadyExistException;
import org.diplom_backend.model.School;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School findById(Long id) {
        return schoolRepository.findById(id).orElseThrow(() ->
                new EntityModelNotFoundException("Школы", "id", Long.toString(id)));
    }

    @Transactional
    public School createSchool(School school) {
        if (schoolRepository.findByName(school.getName()).isPresent()) {
            throw new SchoolAlreadyExistException(school.getName());
        }
        return schoolRepository.save(school);
    }

    @Transactional
    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }
}
