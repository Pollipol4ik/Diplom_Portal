package org.diplom_backend.services;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.model.School;
import org.diplom_backend.model.SchoolClass;
import org.diplom_backend.repositories.SchoolClassRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public SchoolClass createClass(SchoolClass schoolClass, Long schoolId) throws EntityModelNotFoundException {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", Long.toString(schoolId)));

        schoolClass.setSchool(school);
        return schoolClassRepository.save(schoolClass);
    }

    public SchoolClass getSchoolClass(Long id) throws EntityModelNotFoundException {
        return schoolClassRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Школьный класс", "id", Long.toString(id)));
    }

    public List<SchoolClass> getClassesInSchool(Long schoolId) {
        return schoolClassRepository.findAllBySchoolId(schoolId);
    }

    @Transactional
    public void deleteClass(Long id) throws EntityModelNotFoundException {
        schoolClassRepository.delete(getSchoolClass(id));
    }
}