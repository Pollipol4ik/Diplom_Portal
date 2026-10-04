package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.SubjectTopicBySubjectIdRequestDto;
import org.diplom_backend.dto.requests.SubjectTopicRequestDto;
import org.diplom_backend.dto.requests.SubjectTopicUpdateRequestDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.SubjectTopicResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.mappers.SubjectTopicMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.SubjectTopicEntity;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.SubjectTopicService;
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

@RestController
@RequestMapping("/v1/subject-topics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Топик", description = "Работа с топиками предметов")
public class SubjectTopicController {

    private final SubjectTopicService topicService;
    private final SubjectTopicMapper subjectTopicMapper;
    private final PageMapper pageMapper;

    @GetMapping
    @Operation(summary = "Получение всех топиков по предмету")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Успешное получение данных", content = @Content))
    public PageResponseDto<SubjectTopicResponseDto> findAllBySubjectId(
            @ParameterObject @Valid SubjectTopicBySubjectIdRequestDto dto) {
        return pageMapper.toPageResponseDto(
                topicService.findAllBySubjectId(dto.getPageNumber(), dto.getPageSize(), dto.getSubjectId()),
                subjectTopicMapper::getSubjectTopicResponseDTO
        );
    }

    @PostMapping
    @IsModerator
    @Operation(summary = "Создание топика", description = "Создание топика (модератор предмета или администратор)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public SubjectTopicResponseDto createSubjectTopic(
            @Valid @RequestBody SubjectTopicRequestDto dto,
            @AuthenticationPrincipal Account currentAccount) throws EntityModelNotFoundException {
        SubjectTopicEntity savedTopic = topicService.createSubjectTopic(
                subjectTopicMapper.getSubjectTopicFromDTO(dto),
                currentAccount
        );
        return subjectTopicMapper.getSubjectTopicResponseDTO(savedTopic);
    }

    @PutMapping("/{id}")
    @IsModerator
    @Operation(summary = "Редактирование топика (модератор предмета или администратор)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик отредактирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    public SubjectTopicResponseDto updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody SubjectTopicUpdateRequestDto dto,
            @AuthenticationPrincipal Account currentAccount) throws EntityModelNotFoundException {
        return subjectTopicMapper.getSubjectTopicResponseDTO(
                topicService.updateSubjectTopic(id, dto.name(), currentAccount)  // ← берём только name
        );
    }

    @DeleteMapping("/{id}")
    @IsModerator
    @Operation(summary = "Удаление топика (модератор предмета или администратор)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Топик удалён"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal Account currentAccount) throws EntityModelNotFoundException {
        topicService.deleteSubjectTopic(id, currentAccount);
        return ResponseEntity.noContent().build();
    }
}