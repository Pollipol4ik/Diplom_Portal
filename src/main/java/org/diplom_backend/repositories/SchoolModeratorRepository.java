package org.diplom_backend.repositories;

import org.diplom_backend.model.SchoolModerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolModeratorRepository extends JpaRepository<SchoolModerator, Long> {

    /** Существует ли назначение данного модератора на данную школу. */
    boolean existsByAccount_IdAndSchool_Id(Long accountId, Long schoolId);

    /** Все модераторы конкретной школы. */
    List<SchoolModerator> findAllBySchool_Id(Long schoolId);

    /** Все школы, закреплённые за модератором. */
    List<SchoolModerator> findAllByAccount_Id(Long accountId);

    /** Найти конкретную связь (для удаления). */
    Optional<SchoolModerator> findByAccount_IdAndSchool_Id(Long accountId, Long schoolId);

    void deleteByAccount_IdAndSchool_Id(Long accountId, Long schoolId);
}