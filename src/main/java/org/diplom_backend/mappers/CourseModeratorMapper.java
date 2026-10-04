package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.CourseModeratorResponseDto;
import org.diplom_backend.dto.responses.SchoolResponseDto;
import org.diplom_backend.model.CourseModerator;
import org.diplom_backend.model.School;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CourseModeratorMapper {

    @Mapping(target = "accountId",           source = "account.id")
    @Mapping(target = "nickname",            source = "account.nickname")
    @Mapping(target = "courseId",            source = "course.id")
    @Mapping(target = "courseName",          source = "course.name")
    @Mapping(target = "courseDescription",   source = "course.description")
    @Mapping(target = "courseSchools",       source = "course.schools", qualifiedByName = "schoolsToDto")
    @Mapping(target = "isActive",            source = "course.isActive")
    @Mapping(target = "isIntroduction",      source = "course.isIntroduction")
    @Mapping(target = "forLaggingStudents",  source = "course.forLaggingStudents")
    @Mapping(target = "lessonCount",         source = "course.lessons", qualifiedByName = "lessonsSize")
    CourseModeratorResponseDto toResponseDto(CourseModerator entity);

    List<CourseModeratorResponseDto> toResponseDtoList(List<CourseModerator> entities);

    @Named("schoolsToDto")
    static List<SchoolResponseDto> schoolsToDto(Set<School> schools) {
        if (schools == null) return List.of();
        return schools.stream()
                .map(s -> new SchoolResponseDto(s.getId(), s.getName(), s.getTopicDeadline()))
                .toList();
    }

    @Named("lessonsSize")
    static Integer lessonsSize(java.util.List<?> lessons) {
        return lessons == null ? 0 : lessons.size();
    }
}