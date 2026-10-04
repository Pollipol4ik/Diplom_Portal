package org.diplom_backend.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.AccountUpdateRoleRequestDto;
import org.diplom_backend.dto.requests.AdminRegisterUserRequestDto;
import org.diplom_backend.dto.requests.ChangePasswordRequestDto;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.requests.SaveInformationAboutAccountRequestDto;
import org.diplom_backend.dto.responses.AccountCourseProgressResponseDto;
import org.diplom_backend.dto.responses.AccountResponseDto;
import org.diplom_backend.dto.responses.AccountUpdateRoleResponseDto;
import org.diplom_backend.dto.responses.GetAllUserResponseDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.StatusAccountResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.exceptions.RoleNotFoundException;
import org.diplom_backend.mappers.AccountMapper;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsUser;
import org.diplom_backend.services.AccountCourseProgressService;
import org.diplom_backend.services.AccountService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Tag(name = "Аккаунты", description = "Работа с аккаунтами")
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountCourseProgressService courseProgressService;
    private final PageMapper pageMapper;
    private final AccountMapper accountMapper;

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Смена пароля (любой авторизованный пользователь)")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody ChangePasswordRequestDto request) {
        accountService.changePassword(account, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/register")
    @IsAdmin
    @Operation(summary = "Создать учётную запись пользователя")
    public GetAllUserResponseDto adminRegisterUser(@Valid @RequestBody AdminRegisterUserRequestDto request)
            throws RoleNotFoundException {
        Account created = accountService.registerUserByAdmin(request);
        return accountMapper.toGetAllUserResponseDto(created);
    }

    @GetMapping
    @Operation(description = "Получение админом списка всех пользователей", summary = "Получение админом списка всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получение всего списка пользователей"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsAdmin
    public PageResponseDto<GetAllUserResponseDto> getAllUsers(@ParameterObject @Valid PageRequestDto pageRequestDto) {
        return pageMapper.toPageResponseDto(
                accountService.getAllUsers(pageRequestDto.getPageNumber(), pageRequestDto.getPageSize()),
                accountMapper::toGetAllUserResponseDto
        );
    }

    @PostMapping("/{id}/ban")
    @Operation(description = "Заблокировать пользователя", summary = "Заблокировать пользователя")
    @IsAdmin
    public StatusAccountResponseDto banUser(@PathVariable
                                            @Schema(description = "Id пользователя", example = "1")
                                            @Min(1)
                                            @NotNull
                                            Long id) throws EntityModelNotFoundException {
        return accountMapper.toStatusAccountResponseDto(accountService.banUserById(id));
    }

    @PostMapping("/{id}/unban")
    @Operation(description = "Разблокировать пользователя", summary = "Разблокировать пользователя")
    @IsAdmin
    public StatusAccountResponseDto unbanUser(@PathVariable
                                              @Schema(description = "Id пользователя", example = "1")
                                              @Min(1)
                                              @NotNull
                                              Long id) throws EntityModelNotFoundException {
        return accountMapper.toStatusAccountResponseDto(accountService.unbanUserById(id));
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Удалить пользователя (только для администратора)")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long id) throws EntityModelNotFoundException {
        accountService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/profile")
    @IsAdmin
    @Operation(summary = "Обновить ФИО, школу и класс пользователя (только администратор)")
    public GetAllUserResponseDto adminUpdateUser(
            @PathVariable @Min(1) Long id,
            @RequestBody Map<String, Object> body) throws EntityModelNotFoundException {
        String firstName = (String) body.get("first_name");
        String lastName = (String) body.get("last_name");
        String middleName = (String) body.get("middle_name");
        Long schoolId = body.get("school_id") != null ? ((Number) body.get("school_id")).longValue() : null;
        Long classId = body.get("class_id") != null ? ((Number) body.get("class_id")).longValue() : null;
        Account updated = accountService.adminUpdateUser(id, firstName, lastName, middleName, schoolId, classId);
        return accountMapper.toGetAllUserResponseDto(updated);
    }

    @GetMapping(value = "/{nickname}")
    @Operation(description = "Получение информации о пользователе", summary = "Получение информации о пользователе")
    public AccountResponseDto getAccount(@PathVariable
                                         @Schema(description = "Nickname пользователя", example = "admin")
                                         @Size(max = 150, message = "Слишком длинный nickname")
                                         @NotBlank
                                         String nickname) throws EntityModelNotFoundException {
        return accountMapper.toAccountResponseDto(accountService.getAccount(nickname));
    }

    @PutMapping(consumes = {"multipart/form-data"})
    @IsUser
    @Operation(summary = "Сохранение информации профиля", description = "Обновляет данные, включая школу и класс")
    public AccountResponseDto saveInfoAboutAccount(
            @ModelAttribute @Valid SaveInformationAboutAccountRequestDto request) throws EntityModelNotFoundException {

        return accountMapper.toAccountResponseDto(accountService.saveInformationAboutAccount(
                accountMapper.fromSaveInformationAboutAccountRequestDto(request),
                request.schoolId(),
                request.classId(),
                request.photo()
        ));
    }

    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Список учеников школы")
    public PageResponseDto<GetAllUserResponseDto> getBySchool(
            @PathVariable Long schoolId,
            @ParameterObject @Valid PageRequestDto pageDto) {

        return pageMapper.toPageResponseDto(
                accountService.getStudentsBySchool(schoolId, pageDto.getPageNumber(), pageDto.getPageSize()),
                accountMapper::toGetAllUserResponseDto
        );
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Список учеников класса")
    public PageResponseDto<GetAllUserResponseDto> getByClass(
            @PathVariable Long classId,
            @ParameterObject @Valid PageRequestDto pageDto) {

        return pageMapper.toPageResponseDto(
                accountService.getStudentsByClass(classId, pageDto.getPageNumber(), pageDto.getPageSize()),
                accountMapper::toGetAllUserResponseDto
        );
    }

    @PutMapping("/{id}/role")
    @Operation(description = "Обновление роли у пользователя. Это может сделать только админ", summary = "Админ имеет возможность изменить роль пользователя")
    @IsAdmin
    public AccountUpdateRoleResponseDto updateAccountRole(@PathVariable @Valid @Min(0) Long id, @RequestBody @Valid AccountUpdateRoleRequestDto updateRoleRequest) throws RoleNotFoundException {
        Account account = accountService.updateAccountRole(id, updateRoleRequest.roleId());
        return accountMapper.toAccountUpdateRoleResponseDto(account);
    }

    @GetMapping("/{nickname}/course-progress")
    @Operation(summary = "Прогресс пользователя по курсам")
    public List<AccountCourseProgressResponseDto> getCourseProgress(
            @PathVariable String nickname) throws EntityModelNotFoundException {
        Account account = accountService.getAccount(nickname);
        return courseProgressService.getCourseProgress(account);
    }
}