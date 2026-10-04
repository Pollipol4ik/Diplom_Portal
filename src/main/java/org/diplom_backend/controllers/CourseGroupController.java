package org.diplom_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.CreateCourseGroupRequestDto;
import org.diplom_backend.dto.requests.CreateMyCourseGroupRequestDto;
import org.diplom_backend.dto.requests.UpdateCourseGroupRequestDto;
import org.diplom_backend.dto.requests.UpdateCourseGroupTopicRequestDto;
import org.diplom_backend.dto.responses.CourseGroupResponseDto;
import org.diplom_backend.mappers.CourseGroupMapper;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.CourseGroup;
import org.diplom_backend.model.CourseGroupMember;
import org.diplom_backend.security.annotations.IsModerator;
import org.diplom_backend.security.annotations.IsUser;
import org.diplom_backend.services.CourseGroupService;
import org.diplom_backend.services.CourseModeratorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Группы курса", description = "Управление проектными группами внутри курса")
@RequestMapping("/v1/courses/{courseId}/groups")
public class CourseGroupController {

    private final CourseGroupService courseGroupService;
    private final CourseModeratorService courseModeratorService;
    private final CourseGroupMapper courseGroupMapper;

    @PostMapping
    @IsModerator
    @Operation(summary = "Создать группу (проектную команду) в курсе")
    public CourseGroupResponseDto createGroup(
            @PathVariable Long courseId,
            @RequestBody @Valid CreateCourseGroupRequestDto request,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        CourseGroup group = courseGroupService.createGroup(
                courseId, request.title(), request.description(),
                request.schoolId(), request.studentIds(), account, true);
        return courseGroupMapper.toResponseDto(group);
    }

    @GetMapping
    @Operation(summary = "Список всех групп курса")
    public List<CourseGroupResponseDto> getGroups(@PathVariable Long courseId,
                                                  @AuthenticationPrincipal Account account) {
        var groups = (account != null
                && account.getRole() != null
                && account.getRole().getName() == org.diplom_backend.model.Role.ROLE_USER
                && account.getSchool() != null)
                ? courseGroupService.getGroupsByCourseAndSchool(courseId, account.getSchool().getId())
                : courseGroupService.getGroupsByCourse(courseId);

        return groups.stream()
                .map(courseGroupMapper::toResponseDto)
                .toList();
    }

    @PatchMapping("/{groupId}")
    @IsModerator
    @Operation(summary = "Обновить группу (тему, описание, участников)")
    public CourseGroupResponseDto updateGroup(
            @PathVariable Long courseId,
            @PathVariable Long groupId,
            @RequestBody @Valid UpdateCourseGroupRequestDto request,
            @AuthenticationPrincipal Account account) {
        courseModeratorService.verifyModeratorAccessToCourse(account, courseId);
        CourseGroup group = courseGroupService.reassignGroup(
                groupId, request.title(), request.description(),
                request.studentIds(), account);
        return courseGroupMapper.toResponseDto(group);
    }

    @GetMapping("/my")
    @IsUser
    @Operation(summary = "Моя группа в этом курсе")
    public CourseGroupResponseDto getMyGroup(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Account account) {
        CourseGroupMember member = courseGroupService.getMyGroupForCourse(account.getId(), courseId);
        if (member == null) return null;
        return courseGroupMapper.toResponseDto(member.getGroup());
    }

    @PostMapping("/my")
    @IsUser
    @Operation(summary = "Выбрать тему: создать свою группу в курсе (ученик)")
    public CourseGroupResponseDto createMyGroup(
            @PathVariable Long courseId,
            @RequestBody @Valid CreateMyCourseGroupRequestDto request,
            @AuthenticationPrincipal Account account) {
        CourseGroup group = courseGroupService.createMyGroup(
                courseId,
                request.title(),
                request.description(),
                request.classmateAccountIds() != null ? request.classmateAccountIds() : List.of(),
                account
        );
        return courseGroupMapper.toResponseDto(group);
    }

    @PatchMapping("/{groupId}/topic")
    @IsUser
    @Operation(summary = "Изменить название и описание темы своей группы (ученик)")
    public CourseGroupResponseDto updateMyGroupTopic(
            @PathVariable Long courseId,
            @PathVariable Long groupId,
            @RequestBody @Valid UpdateCourseGroupTopicRequestDto request,
            @AuthenticationPrincipal Account account) {
        CourseGroup group = courseGroupService.updateGroupTopicAsStudent(
                courseId, groupId, request.title(), request.description(), account);
        return courseGroupMapper.toResponseDto(group);
    }

    @PostMapping("/{groupId}/members/{accountId}")
    @IsUser
    @Operation(summary = "Добавить участника в группу (ученик-владелец или модератор)")
    public CourseGroupResponseDto addMember(
            @PathVariable Long courseId,
            @PathVariable Long groupId,
            @PathVariable Long accountId,
            @AuthenticationPrincipal Account account) {
        CourseGroup group = courseGroupService.addMemberToGroup(courseId, groupId, accountId, account);
        return courseGroupMapper.toResponseDto(group);
    }

    @DeleteMapping("/{groupId}/members/{accountId}")
    @IsUser
    @Operation(summary = "Удалить участника из группы (ученик-владелец или модератор)")
    public CourseGroupResponseDto removeMember(
            @PathVariable Long courseId,
            @PathVariable Long groupId,
            @PathVariable Long accountId,
            @AuthenticationPrincipal Account account) {
        CourseGroup group = courseGroupService.removeMemberFromGroup(courseId, groupId, accountId, account);
        return courseGroupMapper.toResponseDto(group);
    }
}