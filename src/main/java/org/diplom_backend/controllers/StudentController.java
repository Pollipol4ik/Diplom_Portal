package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.PageRequestDto;
import org.diplom_backend.dto.requests.TransferLaggingRequestDto;
import org.diplom_backend.dto.responses.PageResponseDto;
import org.diplom_backend.dto.responses.StudentRatingResponseDto;
import org.diplom_backend.mappers.PageMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.CourseModeratorService;
import org.diplom_backend.services.LaggingCheckService;
import org.diplom_backend.services.StudentRatingService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ученики", description = "Рейтинг и отстающие ученики")
@RequestMapping("/v1/students")
public class StudentController {

    private final StudentRatingService ratingService;
    private final LaggingCheckService laggingCheckService;
    private final PageMapper pageMapper;
    private final CourseModeratorService courseModeratorService;

    @GetMapping("/rating")
    @IsModerator
    @Operation(summary = "Рейтинг учеников школы (опционально по классу). Без schoolId — все доступные школы")
    public PageResponseDto<StudentRatingResponseDto> getRatings(
            @RequestParam(value = "schoolId", required = false) Long schoolId,
            @RequestParam(value = "classId", required = false) Long classId,
            @ParameterObject @Valid PageRequestDto pageDto,
            @AuthenticationPrincipal Account account) {
        return pageMapper.toPageResponseDto(
                ratingService.getRatings(schoolId, classId,
                        pageDto.getPageNumber(), pageDto.getPageSize(), account),
                r -> r);
    }

    @GetMapping("/lagging")
    @IsModerator
    @Operation(summary = "Отстающие ученики школы. Без schoolId — все доступные школы")
    public PageResponseDto<StudentRatingResponseDto> getLagging(
            @RequestParam(value = "schoolId", required = false) Long schoolId,
            @ParameterObject @Valid PageRequestDto pageDto,
            @AuthenticationPrincipal Account account) {
        return pageMapper.toPageResponseDto(
                ratingService.getLaggingStudents(schoolId,
                        pageDto.getPageNumber(), pageDto.getPageSize(), account),
                r -> r);
    }

    @PostMapping("/lagging/{accountId}/unmark")
    @IsModerator
    @Operation(summary = "Снять отметку отстающего с ученика")
    public Map<String, Object> unmarkLagging(
            @PathVariable Long accountId,
            @AuthenticationPrincipal Account account) {
        laggingCheckService.unmarkLagging(accountId);
        return Map.of("success", true, "message", "Флаг отстающего снят");
    }

    @PostMapping("/{accountId}/transfer")
    @IsModerator
    @Operation(
            summary = "Перевести ученика на целевой курс",
            description = "Работает для любого ученика (обычного или отстающего). " +
                    "Удаляет из текущих целевых групп, создаёт индивидуальную группу в новом курсе. " +
                    "У отстающего снимается флаг is_lagging."
    )
    public Map<String, Object> transferToCourse(
            @PathVariable Long accountId,
            @RequestBody @Valid TransferLaggingRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        laggingCheckService.transferToCourse(accountId, request.targetCourseId(), moderator);
        return Map.of("success", true, "message", "Ученик перенесён на целевой курс");
    }

    /**
     * @deprecated Используйте POST /{accountId}/transfer
     */
    @PostMapping("/lagging/{accountId}/transfer")
    @IsModerator
    @Operation(summary = "Устаревший эндпоинт — используйте /{accountId}/transfer")
    public Map<String, Object> transferToLaggingCourse(
            @PathVariable Long accountId,
            @RequestBody @Valid TransferLaggingRequestDto request,
            @AuthenticationPrincipal Account moderator) {
        laggingCheckService.transferToCourse(accountId, request.targetCourseId(), moderator);
        return Map.of("success", true, "message", "Ученик перенесён на целевой курс");
    }

    @DeleteMapping("/{studentId}/courses/{courseId}/unenroll")
    @IsModerator
    @Operation(summary = "Снять принудительное назначение ученика с курса")
    public ResponseEntity<Void> unassignFromCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @AuthenticationPrincipal Account moderator) {
        courseModeratorService.verifyModeratorAccessToCourse(moderator, courseId);
        laggingCheckService.unassignFromCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }
}