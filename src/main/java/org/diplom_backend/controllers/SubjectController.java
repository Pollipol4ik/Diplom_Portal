package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.SubjectByDirectionIdRequestDto;
import org.diplom_backend.dto.requests.SubjectRequestDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.SubjectModeratorResponseDto;
import org.diplom_backend.dto.responses.SubjectResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.mappers.SubjectMapper;
import org.diplom_backend.mappers.SubjectModeratorMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.AccessControlService;
import org.diplom_backend.services.AdminModeratorService;
import org.diplom_backend.services.SubjectService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/v1/subjects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Предмет", description = "Работа с предметами и их привязкой к направлениям")
public class SubjectController {

    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;
    private final SubjectModeratorMapper subjectModeratorMapper;
    private final AdminModeratorService adminModeratorService;
    private final AccessControlService accessControlService;
    private final PageMapper pageMapper;

    // ── Получение ─────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Получение предметов по направлению")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры", content = @Content)
    })
    public PageResponseDto<SubjectResponseDto> findAllByDirectionId(
            @ParameterObject @Valid SubjectByDirectionIdRequestDto dto) {
        return pageMapper.toPageResponseDto(
                subjectService.findAllByDirectionId(dto.getPageNumber(), dto.getPageSize(), dto.getDirectionId()),
                subjectMapper::toSubjectResponseDTO
        );
    }

    // ── Запись ────────────────────────────────────────────────────────────────

    @PostMapping
    @IsAdmin
    @Operation(summary = "Создание нового предмета")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Предмет успешно создан"),
            @ApiResponse(responseCode = "400", description = "Предмет с таким именем уже существует", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public SubjectResponseDto createSubject(@Valid @RequestBody SubjectRequestDto dto)
            throws EntityModelNotFoundException {
        SubjectEntity subject = subjectMapper.fromSubjectRequestDTO(dto);
        return subjectMapper.toSubjectResponseDTO(subjectService.createSubject(subject));
    }

    @PutMapping("/{id}")
    @IsModerator
    @Operation(summary = "Редактирование предмета (администратор или назначенный модератор)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Предмет отредактирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    public SubjectResponseDto updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequestDto dto,
            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        // Проверяем права: admin всегда ок, модератор — только если назначен на этот предмет
        accessControlService.verifyModeratorAccess(account, id);
        return subjectMapper.toSubjectResponseDTO(
                subjectService.updateSubject(id, dto.name())
        );
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    @Operation(summary = "Удаление предмета (каскадно удаляет темы и публикации)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Предмет удалён"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Предмет не найден")
    })
    public ResponseEntity<Void> deleteSubject(@Valid @PathVariable @Min(0) Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}