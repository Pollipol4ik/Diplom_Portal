package org.diplom_backend.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.ChangeCommentRequestDto;
import org.diplom_backend.dto.requests.CommentAudRequestDto;
import org.diplom_backend.dto.requests.CommentsOnThePublicationRequestDto;
import org.diplom_backend.dto.requests.CreateCommentRequestDto;
import org.diplom_backend.dto.requests.CreateThreadRequestDto;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.responses.CommentResponseDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.CommentMapper;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CommentAudit;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.security.annotations.IsUser;
import org.diplom_backend.services.CommentAudService;
import org.diplom_backend.services.CommentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "Работа с комментариями")
@RequestMapping("/v1/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentAudService commentAudService;
    private final CommentMapper commentMapper;
    private final PageMapper pageMapper;

    @PostMapping
    @Operation(description = "Добавить новый комментарий к публикации", summary = "Добавить новый комментарий к публикации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно добавлен новый комментарий к публикации"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsUser
    public CommentResponseDto createComment(@RequestBody @Valid CreateCommentRequestDto request,
                                            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return commentMapper.toCommentResponseDto(
                commentService.createComment(commentMapper.fromCreateCommentRequestDto(request), account)
        );
    }

    @GetMapping("/{id}")
    @Operation(description = "Найти комментарий по id", summary = "Найти комментарий по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно найден комментарий по id"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
    })
    public CommentResponseDto getComment(@PathVariable
                                         @Min(value = 1, message = "Id комментария не может быть меньше 1")
                                         @NotNull(message = "Id комментария не может быть пустым")
                                         @Schema(description = "Id комментария", example = "1")
                                         Long id) throws EntityModelNotFoundException {
        return commentMapper.toCommentResponseDto(commentService.getComment(id));
    }

    @PutMapping
    @Operation(description = "Изменить содержимое комментария", summary = "Изменить содержимое комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно изменен комментарий"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsUser
    public CommentResponseDto updateComment(@RequestBody @Valid ChangeCommentRequestDto request,
                                            @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return commentMapper.toCommentResponseDto(
                commentService.updateComment(commentMapper.fromChangeCommentRequestDto(request), account.getNickname())
        );
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Удалить комментарий по id", summary = "Удалить комментарий по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешно удален комментарий по id"), // Обычно 204 для Delete
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsModerator
    public void deleteComment(@PathVariable
                              @Min(value = 1, message = "Id комментария не может быть меньше 1")
                              @NotNull(message = "Id комментария не может быть пустым")
                              @Schema(description = "Id комментария", example = "1")
                              Long id,
                              @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        // Передаем текущего пользователя в сервис для проверки прав модератора
        commentService.deleteComment(id, account);
    }

    @GetMapping
    @Operation(description = "Получить все комментарии к публикации",
            summary = "Получить все комментарии к публикации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все комментарии к публикации"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public PageResponseDto<CommentResponseDto> getCommentsOnThePublication(@ParameterObject @Valid CommentsOnThePublicationRequestDto request) {
        return pageMapper.toPageResponseDto(commentService.getCommentsOnThePublication(request.getPageNumber(), request.getPageSize(), request.getPublicationId()),
                commentMapper::toCommentResponseDto);
    }

    @PostMapping("/create-comment-reply")
    @Operation(description = "Добавить новый тред", summary = "Добавить новый тред")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно добавлен новый тред"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsUser
    public CommentResponseDto createThread(@RequestBody @Valid CreateThreadRequestDto request,
                                           @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return commentMapper.toCommentResponseDto(
                commentService.createCommentThread(commentMapper.fromCreateThreadRequestDto(request), account)
        );
    }

    @GetMapping("/{id}/replies")
    @Operation(description = "Получить все треды к комментарию",
            summary = "Получить все треды к комментарию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все треды к комментарию"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public PageResponseDto<CommentResponseDto> getThreadsOnTheComment(@ParameterObject @Valid PageRequestDto request,
                                                                      @PathVariable
                                                                      @Min(value = 1, message = "Id комментария не может быть меньше 1")
                                                                      @NotNull(message = "Id комментария не может быть пустым")
                                                                      @Schema(description = "Id комментария", example = "1")
                                                                      Long id) {
        return pageMapper.toPageResponseDto(commentService.getThreadsOnTheComment(request.getPageNumber(), request.getPageSize(), id),
                commentMapper::toCommentResponseDto);
    }


    @GetMapping("/revisions")
    @Operation(description = "Получить ревезии",
            summary = "Получить ревезии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены ревезии"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Нет прав")
    })
    @IsAdmin
    public List<CommentAudit> getRevisions(@ParameterObject @Valid CommentAudRequestDto commentAudRequestDto) {
        return commentAudService.getRevisionForComment(commentAudRequestDto.getCommentId());
    }
}
