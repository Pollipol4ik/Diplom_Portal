package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.responses.CourseModeratorResponseDto;
import org.diplom_backend.mappers.CourseModeratorMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.security.annotations.IsAdmin;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.services.CourseModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/course-moderators")
@RequiredArgsConstructor
@Tag(name = "Админка: Модераторы курсов", description = "Назначение модераторов на курсы для проверки заданий")
public class AdminCourseModeratorController {

    private final CourseModeratorService courseModeratorService;
    private final CourseModeratorMapper mapper;

    @PostMapping("/assign")
    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Назначить модератора на курс")
    public CourseModeratorResponseDto assign(
            @RequestParam Long accountId,
            @RequestParam Long courseId) {
        return mapper.toResponseDto(courseModeratorService.assignModerator(accountId, courseId));
    }

    @GetMapping("/course/{courseId}")
    @IsAdmin
    @Operation(summary = "Модераторы курса")
    public List<CourseModeratorResponseDto> listByCourse(@PathVariable Long courseId) {
        return mapper.toResponseDtoList(courseModeratorService.getModeratorsByCourse(courseId));
    }

    @GetMapping("/moderator/{accountId}")
    @IsAdmin
    @Operation(summary = "Курсы модератора (для админа)")
    public List<CourseModeratorResponseDto> listByModerator(@PathVariable Long accountId) {
        return mapper.toResponseDtoList(courseModeratorService.getCoursesByModerator(accountId));
    }

    @GetMapping("/my-courses")
    @IsModerator
    @Operation(summary = "Мои назначенные курсы (для модератора)")
    public List<CourseModeratorResponseDto> myAssignedCourses(@AuthenticationPrincipal Account account) {
        return mapper.toResponseDtoList(courseModeratorService.getCoursesByModerator(account.getId()));
    }

    @DeleteMapping("/remove")
    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Снять модератора с курса")
    public void remove(@RequestParam Long accountId, @RequestParam Long courseId) {
        courseModeratorService.removeModerator(accountId, courseId);
    }
}
