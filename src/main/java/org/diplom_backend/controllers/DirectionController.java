package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.CreateDirectionRequestDto;
import org.diplom_backend.dto.responses.DirectionResponseDto;
import org.diplom_backend.mappers.DirectionMapper;
import org.diplom_backend.model.DirectionEntity;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.services.DirectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/directions")
@RequiredArgsConstructor
@Tag(name = "Направление", description = "Методы для управления направлениями обучения")
public class DirectionController {

    private final DirectionService directionService;
    private final DirectionMapper directionMapper;

    @GetMapping
    @Operation(summary = "Получение всех направлений обучения")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Список успешно получен"))
    public List<DirectionResponseDto> getAllDirections() {
        List<DirectionEntity> directions = directionService.findAll();
        return directionMapper.toDirectionResponseDtoList(directions);
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = "Создание нового направления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Направление успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public DirectionResponseDto create(@RequestBody @Valid CreateDirectionRequestDto dto) {
        return directionMapper.toDirectionResponseDto(
                directionService.createDirection(dto.name())
        );
    }

    @PutMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Редактирование направления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Направление успешно отредактировано"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Направление не найдено")
    })
    public DirectionResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateDirectionRequestDto dto) {
        return directionMapper.toDirectionResponseDto(
                directionService.updateDirection(id, dto.name())
        );
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Удаление направления")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Направление успешно удалено"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Направление не найдено")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        directionService.deleteDirection(id);
        return ResponseEntity.noContent().build();
    }
}