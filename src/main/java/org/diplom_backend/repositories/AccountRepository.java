package org.diplom_backend.repositories;


import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByNickname(String nickname);

    Page<Account> findByRole_NameNot(Role role, Pageable pageable);


    Page<Account> findBySchoolId(Long schoolId, Pageable pageable);


    Page<Account> findBySchoolClassId(Long classId, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.schoolClass.id = :classId AND a.school.id IN :schoolIds AND a.role.name = org.diplom_backend.model.Role.ROLE_USER")
    Page<Account> findBySchoolClassIdAndSchool_IdIn(@Param("classId") Long classId,
                                                    @Param("schoolIds") Collection<Long> schoolIds,
                                                    Pageable pageable);

    long countBySchool_Id(Long schoolId);
    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.role.name = org.diplom_backend.model.Role.ROLE_USER")
    Page<Account> findAllStudentsPage(Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.school.id IN :schoolIds AND a.role.name = org.diplom_backend.model.Role.ROLE_USER")
    Page<Account> findStudentsBySchoolIds(@Param("schoolIds") Collection<Long> schoolIds, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.school.id = :schoolId AND a.role.name = org.diplom_backend.model.Role.ROLE_USER AND a.isLagging = true")
    Page<Account> findLaggingStudentsBySchool(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.school.id IN :schoolIds AND a.role.name = org.diplom_backend.model.Role.ROLE_USER AND a.isLagging = true")
    Page<Account> findLaggingStudentsBySchoolIds(@Param("schoolIds") Collection<Long> schoolIds, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.role.name = org.diplom_backend.model.Role.ROLE_USER AND a.isLagging = true")
    Page<Account> findAllLaggingStudents(Pageable pageable);

    /** Ученики школы без членства в проектной группе этой школы (по дедлайну темы — кандидаты в отстающие). */
    @Query("SELECT a FROM Account a WHERE a.school.id = :schoolId " +
           "AND a.role.name = org.diplom_backend.model.Role.ROLE_USER " +
           "AND NOT EXISTS (SELECT 1 FROM CourseGroupMember m WHERE m.account.id = a.id AND m.group.school.id = :schoolId)")
    List<Account> findStudentsWithoutGroupForSchool(@Param("schoolId") Long schoolId);

    @Query("SELECT a FROM Account a WHERE a.school.id = :schoolId AND a.role.name = org.diplom_backend.model.Role.ROLE_USER")
    List<Account> findAllStudentsOfSchool(@Param("schoolId") Long schoolId);

    @Query("SELECT a FROM Account a WHERE a.school.id = :schoolId AND a.role.name = org.diplom_backend.model.Role.ROLE_USER AND a.isLagging = true")
    List<Account> findLaggingStudentsOfSchool(@Param("schoolId") Long schoolId);

    @Modifying
    @Query("UPDATE Account a SET a.isLagging = false WHERE a.school.id = :schoolId " +
           "AND EXISTS (SELECT 1 FROM CourseGroupMember m WHERE m.account.id = a.id AND m.group.school.id = :schoolId)")
    int clearLaggingFlagForStudentsInSchoolGroups(@Param("schoolId") Long schoolId);
}

