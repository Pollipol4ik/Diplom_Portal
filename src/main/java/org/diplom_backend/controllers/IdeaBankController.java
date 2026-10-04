package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.CreateIdeaBankEntryRequestDto;
import org.diplom_backend.dto.requests.AssignIdeaToGroupRequestDto;
import org.diplom_backend.dto.requests.AssignIdeaToStudentRequestDto;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.requests.UpdateIdeaBankEntryRequestDto;
import org.diplom_backend.dto.responses.IdeaBankEntryResponseDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.IdeaBankMapper;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.CourseGroupService;
import org.diplom_backend.services.CourseModeratorService;
import org.diplom_backend.services.IdeaBankService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Банк идей", description = "Архив тем проектов для повторного использования")
@RequestMapping("/v1/idea-bank")
public class IdeaBankController {

    private final IdeaBankService ideaBankService;
    private final IdeaBankMapper ideaBankMapper;
    private final PageMapper pageMapper;
    private final CourseGroupService courseGroupService;
    private final CourseModeratorService courseModeratorService;

    @PostMapping
    @IsModerator
    @Operation(summary = "Добавить идею в банк")
    public IdeaBankEntryResponseDto create(
            @RequestBody @Valid CreateIdeaBankEntryRequestDto request,
            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return ideaBankMapper.toResponseDto(
                ideaBankService.create(request.title(), request.description(), request.comments(),
                        request.score(), request.sourceProjectId(), account));
    }

    @GetMapping
    @IsModerator
    @Operation(summary = "Идеи банка (постранично), фильтрация по курсу, сортировка")
    public PageResponseDto<IdeaBankEntryResponseDto> getAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "date_desc") String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean sortByScore,
            @ParameterObject @Valid PageRequestDto pageDto,
            @AuthenticationPrincipal Account account) {
        // Если используется старый параметр sortByScore — маппируем в новый
        String effectiveSortBy = sortBy;
        if ("date_desc".equals(sortBy) && sortByScore) {
            effectiveSortBy = "score_desc";
        }
        return pageMapper.toPageResponseDto(
                ideaBankService.getAll(query, minScore, maxScore, courseId, effectiveSortBy,
                        pageDto.getPageNumber(), pageDto.getPageSize()),
                ideaBankMapper::toResponseDto);
    }

    @GetMapping("/{id}")
    @IsModerator
    @Operation(summary = "Получить идею по id")
    public IdeaBankEntryResponseDto getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account) {
        return ideaBankMapper.toResponseDto(ideaBankService.getById(id));
    }

    @PatchMapping("/{id}")
    @IsModerator
    @Operation(summary = "Обновить идею")
    public IdeaBankEntryResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateIdeaBankEntryRequestDto request,
            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return ideaBankMapper.toResponseDto(
                ideaBankService.update(id, request.title(), request.description(),
                        request.comments(), request.score(), account));
    }

    @DeleteMapping("/{id}")
    @IsModerator
    @Operation(summary = "Удалить идею из банка")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        ideaBankService.delete(id, account);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/assign-to-group")
    @IsModerator
    @Operation(summary = "Передать идею группе (обновить тему/описание группы)")
    public IdeaBankEntryResponseDto assignToGroup(
            @PathVariable Long id,
            @RequestBody @Valid AssignIdeaToGroupRequestDto request,
            @AuthenticationPrincipal Account account) {
        var idea = ideaBankService.getById(id);
        var group = courseGroupService.getGroupById(request.groupId());
        courseModeratorService.verifyModeratorAccessToCourse(account, group.getCourse().getId());
        courseGroupService.applyIdeaToGroup(group.getId(), idea.getTitle(), idea.getDescription(), account);
        courseGroupService.appendIdeaHistoryToCurrentTopic(group.getId(),
                idea.getDescription(), idea.getComments(), idea.getScore(), account);
        ideaBankService.delete(id, account);
        return ideaBankMapper.toResponseDto(idea);
    }

    @PostMapping("/{id}/assign-to-student")
    @IsModerator
    @Operation(summary = "Назначить идею: создать группу по идее (ученик должен быть без темы)")
    public IdeaBankEntryResponseDto assignToStudent(
            @PathVariable Long id,
            @RequestBody @Valid AssignIdeaToStudentRequestDto request,
            @AuthenticationPrincipal Account account) {
        var idea = ideaBankService.getById(id);
        courseModeratorService.verifyModeratorAccessToCourse(account, request.courseId());
        courseGroupService.createGroupForStudentFromIdea(
                request.courseId(),
                request.studentId(),
                idea.getTitle(),
                idea.getDescription(),
                account
        );
        ideaBankService.delete(id, account);
        return ideaBankMapper.toResponseDto(idea);
    }
}