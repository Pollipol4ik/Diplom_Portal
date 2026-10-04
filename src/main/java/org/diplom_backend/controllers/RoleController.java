package org.diplom_backend.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.RoleResponseDto;
import org.diplom_backend.mappers.RoleMapper;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Роли", description = "Работа с ролями")
@RequestMapping("/v1/roles")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    @Operation(description = "Все роли системы (включая администратора) для назначения пользователям",
            summary = "Список ролей для админ-панели")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены роли"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsAdmin
    public List<RoleResponseDto> getAllRolesForAdminAssignment() {
        return roleService
                .getAllRolesForAssignment()
                .stream()
                .map(roleMapper::toRoleResponseDto)
                .toList();
    }
}
