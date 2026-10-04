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
import org.diplom_backend.dto.requests.CreatePublicationRequestDto;
import org.diplom_backend.dto.requests.PublicationsInOneCategoryRequestDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.PublicationResponseDto;
import org.diplom_backend.dto.responses.PublicationTitleAndIdResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.mappers.PublicationMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.PublicationService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Публикации", description = "Работа с публикациями")
@RequestMapping("/v1/publications")
public class PublicationController {
    private final PublicationService publicationService;
    private final PublicationMapper publicationMapper;
    private final PageMapper pageMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(description = "Добавить новую публикацию", summary = "Добавить новую публикацию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно добавлена новая публикация"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsModerator
    public PublicationResponseDto createPublication(@ModelAttribute @Valid CreatePublicationRequestDto request,
                                                    @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return publicationMapper.toPublicationResponseDto(
                publicationService.createPublicationInSubjectTopic(publicationMapper.fromCreatePublicationRequestDto(request), account, request.files())
        );
    }

    @GetMapping("/{id}")
    @Operation(description = "Найти публикацию по id", summary = "Найти публикацию по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получена публикация"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public PublicationResponseDto getPublication(@PathVariable
                                                 @Min(value = 1, message = "Id публикации не может быть меньше 1")
                                                 @NotNull(message = "Id публикации не может быть пустой")
                                                 @Schema(description = "Id публикации", example = "1")
                                                 Long id) throws EntityModelNotFoundException {
        return publicationMapper.toPublicationResponseDto(publicationService.getPublication(id));
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Удалить публикацию по id", summary = "Удалить публикацию по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно удалена публикация"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsModerator
    public void deletePublication(
            @PathVariable Long id,
            @AuthenticationPrincipal Account currentAccount) throws EntityModelNotFoundException {
        publicationService.deletePublication(id, currentAccount);
    }

    @GetMapping
    @Operation(description = "Найти все публикации в заданном топике по id",
            summary = "Найти все публикации в заданном топике по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все публикации в заданном топике по id"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public PageResponseDto<PublicationTitleAndIdResponseDto> getPublicationsInOneTopic(@ParameterObject @Valid PublicationsInOneCategoryRequestDto publications) {
        return pageMapper.toPageResponseDto(publicationService.getPublicationsInOneCategory(
                        publications.getPageNumber(),
                        publications.getPageSize(),
                        publications.getSubjectTopicId()),
                publicationMapper::toPublicationTitleAndIdResponseDto);
    }

}
