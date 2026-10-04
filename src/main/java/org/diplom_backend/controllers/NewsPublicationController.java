package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.CreateNewsPublicationRequestDto;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.PublicationResponseDto;
import org.diplom_backend.exceptions.EntityModelNotFoundException;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.mappers.PublicationMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.NewsPublicationService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Новостная лента", description = "Работа с новостной лентой")
@RequestMapping("/v1/news_publications")
public class NewsPublicationController {
    private final NewsPublicationService newsPublicationService;
    private final PublicationMapper publicationMapper;
    private final PageMapper pageMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(description = "Добавить новую публикацию в новости", summary = "Добавить новую публикацию в новости")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно добавлена новая публикация"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @IsModerator
    public PublicationResponseDto createPublication(@ModelAttribute @Valid CreateNewsPublicationRequestDto request,
                                                    @AuthenticationPrincipal Account account) throws EntityModelNotFoundException {
        return publicationMapper.toPublicationResponseDto(
                newsPublicationService.createPublicationInNews(publicationMapper.fromCreateNewsPublicationRequestDto(request), account, request.files())
        );
    }

    @GetMapping
    @Operation(description = "Найти все новостные публикации",
            summary = "Найти все новостные публикации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены все публикации"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public PageResponseDto<PublicationResponseDto> getNewsPublications(@ParameterObject @Valid PageRequestDto request) {
        return pageMapper.toPageResponseDto(newsPublicationService.getPublicationsInNews(request.getPageNumber(), request.getPageSize()),
                publicationMapper::toPublicationResponseDto);
    }
}
