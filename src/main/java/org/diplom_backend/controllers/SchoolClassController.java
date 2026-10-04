package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.SchoolClassRequestDto;
import org.diplom_backend.dto.responses.SchoolClassResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.SchoolClassMapper;
import org.diplom_backend.model.SchoolClass;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.SchoolClassService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Школьные классы", description = "Управление классами внутри школ")
@RequestMapping("/v1/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;
    private final SchoolClassMapper schoolClassMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @IsAdmin
    @Operation(summary = "Создание нового класса", description = "Создает класс и привязывает его к существующей школе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Класс успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "404", description = "Указанная школа не найдена")
    })
    public SchoolClassResponseDto createClass(@Valid @RequestBody SchoolClassRequestDto requestDto) throws EntityModelNotFoundException {
        return schoolClassMapper.toResponseDto(
                schoolClassService.createClass(
                        schoolClassMapper.toEntity(requestDto),
                        requestDto.schoolId()
                )
        );
    }

    @GetMapping("/school/{schoolId}")
    @Operation(summary = "Получение списка классов школы", description = "Возвращает все классы для конкретной школы по её ID")
    public List<SchoolClassResponseDto> getClassesInSchool(@PathVariable Long schoolId) {
        return schoolClassService.getClassesInSchool(schoolId)
                .stream()
                .map(schoolClassMapper::toResponseDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsAdmin
    @Operation(summary = "Удаление класса", description = "Удаляет школьный класс по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Класс не найден")
    })
    public void deleteClass(@PathVariable Long id) throws EntityModelNotFoundException {
        schoolClassService.deleteClass(id);
    }
}