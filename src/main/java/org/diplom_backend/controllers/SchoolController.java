package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.SchoolRequestDto;
import org.diplom_backend.dto.responses.SchoolResponseDto;
import org.diplom_backend.mappers.SchoolMapper;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.security.annotations.IsUser;
import org.diplom_backend.services.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Школы", description = "Управление списком учебных заведений")
@RequestMapping("/v1/schools")
public class SchoolController {
    private final SchoolService schoolService;
    private final SchoolMapper schoolMapper;

    @GetMapping
    @Operation(summary = "Получение списка всех школ", description = "Возвращает список всех зарегистрированных учебных заведений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список школ успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @IsUser
    public List<SchoolResponseDto> getAllSchools() {
        return schoolService.getAllSchools()
                .stream()
                .map(schoolMapper::toSchoolResponseDto)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Создание новой школы", description = "Позволяет администратору добавить новую школу в систему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Школа успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или школа уже существует"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsModerator
    public SchoolResponseDto createSchool(@RequestBody SchoolRequestDto schoolResponseDto) {
        return schoolMapper
                .toSchoolResponseDto(schoolService.
                        createSchool(schoolMapper.toEntity(schoolResponseDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsAdmin
    @Operation(summary = "Удаление школы", description = "Удаляет школу из системы по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Школа успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Школа не найдена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public void deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
    }
}
