package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.SubjectModeratorResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.SubjectModeratorMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.AdminModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/moderators")
@RequiredArgsConstructor
@Tag(name = "Админка: Управление модераторами", description = "Назначение модераторов на конкретные предметы")
public class AdminModeratorController {

    private final AdminModeratorService adminService;
    private final SubjectModeratorMapper mapper;

    @PostMapping("/assign")
    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Назначить модератора на предмет", description = "Доступно только администратору. Связывает существующего модератора с дисциплиной.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное назначение"),
            @ApiResponse(responseCode = "400", description = "Пользователь не модератор или уже назначен"),
            @ApiResponse(responseCode = "404", description = "Аккаунт или предмет не найден")
    })
    public SubjectModeratorResponseDto assign(@RequestParam Long accountId, @RequestParam Long subjectId)
            throws EntityModelNotFoundException {
        var result = adminService.assignModerator(accountId, subjectId);
        return mapper.toDto(result);
    }

    @GetMapping("/subject/{id}")
    @IsAdmin
    @Operation(summary = "Список модераторов предмета", description = "Возвращает всех модераторов, закрепленных за конкретным предметом")
    public List<SubjectModeratorResponseDto> listBySubject(@PathVariable Long id) {
        return mapper.toDtoList(adminService.getModeratorsBySubject(id));
    }

    @DeleteMapping("/remove")
    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отозвать права модератора на предмет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Права успешно отозваны"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    public void remove(@RequestParam Long accountId, @RequestParam Long subjectId) {
        adminService.removeModerator(accountId, subjectId);
    }


    @GetMapping("/my")
    @IsModerator
    @Operation(summary = "Получить все предметы, на которые назначен текущий модератор")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список предметов текущего модератора"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется роль модератора)")
    })
    public List<SubjectModeratorResponseDto> getMySubjects(@AuthenticationPrincipal Account account) {
        return mapper.toDtoList(adminService.getMySubjects(account));
    }
}