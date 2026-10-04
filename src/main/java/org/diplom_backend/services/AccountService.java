package org.diplom_backend.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.AdminRegisterUserRequestDto;
import org.diplom_backend.exceptions.AccountAlreadyExistException;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.NotEnoughRightsException;
import org.diplom_backend.exceptions.RoleNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.RoleEntity;
import org.diplom_backend.model.School;
import org.diplom_backend.model.SchoolClass;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.repositories.SchoolClassRepository;
import org.diplom_backend.repositories.SchoolRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final FileService fileService;
    private final RoleService roleService;
    private final SchoolRepository schoolRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Account> getAllUsers(Integer pageNumber, Integer pageSize) {
        return accountRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id")));
    }

    @Transactional
    public Account banUserById(Long id) throws EntityModelNotFoundException {
        var account = accountRepository.findById(id).orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", Long.toString(id)));
        account.setIsBanned(true);
        return accountRepository.save(account);
    }

    @Transactional
    public Account unbanUserById(Long id) throws EntityModelNotFoundException {
        var account = accountRepository.findById(id).orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", Long.toString(id)));
        account.setIsBanned(false);
        return accountRepository.save(account);
    }


    @Transactional
    public void deleteUserById(Long id) throws EntityModelNotFoundException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", Long.toString(id)));

        entityManager.createNativeQuery(
                        "UPDATE hearing_review SET moderator_id = NULL WHERE moderator_id = :id")
                .setParameter("id", id)
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        accountRepository.deleteById(id);
    }

    @Transactional
    public Account adminUpdateUser(Long id, String firstName, String lastName, String middleName,
                                   Long schoolId, Long classId) throws EntityModelNotFoundException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", Long.toString(id)));

        if (firstName != null) account.setFirstName(firstName.isBlank() ? null : firstName.trim());
        if (lastName != null) account.setLastName(lastName.isBlank() ? null : lastName.trim());
        if (middleName != null) account.setMiddleName(middleName.isBlank() ? null : middleName.trim());

        if (schoolId != null) {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));
            account.setSchool(school);
            if (account.getSchoolClass() != null && !account.getSchoolClass().getSchool().getId().equals(schoolId)) {
                account.setSchoolClass(null);
            }
        }

        if (classId != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(classId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Класс", "id", classId.toString()));
            if (account.getSchool() != null && !schoolClass.getSchool().getId().equals(account.getSchool().getId())) {
                throw new IllegalArgumentException("Класс не принадлежит выбранной школе");
            }
            account.setSchoolClass(schoolClass);
        }

        return accountRepository.save(account);
    }

    @Transactional
    public Account saveInformationAboutAccount(Account information, Long schoolId, Long classId, MultipartFile photo)
            throws EntityModelNotFoundException {

        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var accountAuth = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "почтой", email));

        if (!accountAuth.getNickname().equals(information.getNickname())) {
            throw new NotEnoughRightsException();
        }

        accountAuth.setDescription(information.getDescription());
        accountAuth.setFirstName(information.getFirstName());
        accountAuth.setLastName(information.getLastName());
        accountAuth.setMiddleName(information.getMiddleName());
        accountAuth.setBirthDate(information.getBirthDate());

        if (schoolId != null) {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", schoolId.toString()));
            accountAuth.setSchool(school);
        }

        if (classId != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(classId)
                    .orElseThrow(() -> new EntityModelNotFoundException("Класс", "id", classId.toString()));
            if (accountAuth.getSchool() != null && !schoolClass.getSchool().getId().equals(accountAuth.getSchool().getId())) {
                throw new RuntimeException("Выбранный класс не принадлежит вашей школе");
            }
            accountAuth.setSchoolClass(schoolClass);
        }

        if (photo != null) {
            var photoInProfile = fileService.store(photo);
            if (accountAuth.getProfilePhoto() != null) {
                accountAuth.getProfilePhoto().setFileNameInDirectory(photoInProfile.getFileNameInDirectory());
                accountAuth.getProfilePhoto().setInitialFileName(photoInProfile.getInitialFileName());
            } else {
                accountAuth.setProfilePhoto(photoInProfile);
            }
        }

        return accountRepository.save(accountAuth);
    }

    @Transactional
    public Account getAccountById(Long id) throws EntityModelNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "id", id.toString()));
    }

    public Page<Account> getStudentsBySchool(Long schoolId, Integer page, Integer size) {
        return accountRepository.findBySchoolId(schoolId, PageRequest.of(page, size, Sort.by("lastName")));
    }

    public Page<Account> getStudentsByClass(Long classId, Integer page, Integer size) {
        return accountRepository.findBySchoolClassId(classId, PageRequest.of(page, size, Sort.by("lastName")));
    }

    @Transactional
    public Account getAccount(String nickname) throws EntityModelNotFoundException {
        return accountRepository.findByNickname(nickname).orElseThrow(() ->
                new EntityModelNotFoundException("Пользователя", "nickname", nickname)
        );
    }

    @Transactional
    public Account getAccountByEmail(String email) throws EntityModelNotFoundException {
        return accountRepository.findByEmail(email).orElseThrow(() ->
                new EntityModelNotFoundException("Пользователя", "почтой", email)
        );
    }

    @Transactional
    public Account updateAccountRole(Long accountId, Long roleId) throws RoleNotFoundException {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityModelNotFoundException("Пользователя", "id", String.valueOf(accountId))
        );
        RoleEntity role = roleService.getRoleById(roleId);
        account.setRole(role);
        return account;
    }

    @Transactional
    public void changePassword(Account principal, String currentPassword, String newPassword) {
        AuthService.validatePasswordStrength(newPassword);
        Account account = accountRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new EntityModelNotFoundException("Пользователя", "email", principal.getEmail()));
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("Неверный текущий пароль");
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    @Transactional
    public Account registerUserByAdmin(AdminRegisterUserRequestDto dto) throws RoleNotFoundException {
        String email = dto.email().trim();
        String nickname = dto.nickname().trim();
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new AccountAlreadyExistException("почтой", email);
        }
        if (accountRepository.findByNickname(nickname).isPresent()) {
            throw new AccountAlreadyExistException("ником", nickname);
        }
        RoleEntity role = dto.roleId() != null
                ? roleService.getRoleById(dto.roleId())
                : roleService.getRoleByName(Role.ROLE_USER);
        Account account = new Account();
        account.setEmail(email);
        account.setNickname(nickname);
        account.setPassword(passwordEncoder.encode(dto.password()));
        account.setRole(role);
        account.setIsBanned(false);
        account.setIsLagging(false);
        if (dto.schoolId() != null) {
            School school = schoolRepository.findById(dto.schoolId())
                    .orElseThrow(() -> new EntityModelNotFoundException("Школа", "id", dto.schoolId().toString()));
            account.setSchool(school);
        }
        if (dto.classId() != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(dto.classId())
                    .orElseThrow(() -> new EntityModelNotFoundException("Класс", "id", dto.classId().toString()));
            if (account.getSchool() != null && !schoolClass.getSchool().getId().equals(account.getSchool().getId())) {
                throw new IllegalArgumentException("Класс не принадлежит выбранной школе");
            }
            account.setSchoolClass(schoolClass);
        }
        return accountRepository.save(account);
    }
}