package org.diplom_backend.config;

import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.RoleNotFoundException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.RoleEntity;
import org.diplom_backend.services.AuthService;
import org.diplom_backend.services.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AuthService authService;
    private final RoleService roleService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.nickname}")
    private String nickname;

    @Override
    public void run(ApplicationArguments args) throws RoleNotFoundException {
        insertRoles();
        registerAdmin();
    }


    private void insertRoles() {
        Role[] roles = Role.values();
        for (Role role : roles) {
            if (!roleService.roleExists(role)) {
                RoleEntity roleEntity = new RoleEntity(null, role, null);
                roleService.save(roleEntity);
            }
        }
    }

    private void registerAdmin() {
        authService.registerAdmin(
                new Account(null,
                        adminEmail,
                        password,
                        nickname,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.now(),
                        false,
                        false,
                        LocalDateTime.now(),
                        roleService.getRoleByName(Role.ROLE_ADMIN),
                        null,
                        null,
                        null,
                        null,
                        null));
    }

}