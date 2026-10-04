package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.SchoolModeratorResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.SchoolModeratorMapper;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.SchoolModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/school-moderators")
@RequiredArgsConstructor
@Tag(name = "Админка: Модераторы школ", description = "Назначение модераторов на школы для проверки проектных работ")
public class AdminSchoolModeratorController {

    private final SchoolModeratorService schoolModeratorService;
    private final SchoolModeratorMapper mapper;

    @PostMapping("/assign")
    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Назначить модератора на школу")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Модератор успешно назначен"),
            @ApiResponse(responseCode = "400", description = "Пользователь не модератор или уже назначен"),
            @ApiResponse(responseCode = "404", description = "Аккаунт или школа не найдены")
    })
    public SchoolModeratorResponseDto assign(
            @RequestParam Long accountId,
            @RequestParam Long schoolId) throws EntityModelNotFoundException {
        return mapper.toResponseDto(schoolModeratorService.assignModerator(accountId, schoolId));
    }

    @GetMapping("/school/{schoolId}")
    @IsAdmin
    @Operation(summary = "Список модераторов школы")
    public List<SchoolModeratorResponseDto> listBySchool(@PathVariable Long schoolId) {
        return mapper.toResponseDtoList(schoolModeratorService.getModeratorsBySchool(schoolId));
    }

    @GetMapping("/moderator/{accountId}")
    @IsAdmin
    @Operation(summary = "Список школ, закреплённых за модератором")
    public List<SchoolModeratorResponseDto> listByModerator(@PathVariable Long accountId) {
        return mapper.toResponseDtoList(schoolModeratorService.getSchoolsByModerator(accountId));
    }

    @DeleteMapping("/remove")
    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Снять модератора со школы")
    public void remove(@RequestParam Long accountId, @RequestParam Long schoolId) {
        schoolModeratorService.removeModerator(accountId, schoolId);
    }
}