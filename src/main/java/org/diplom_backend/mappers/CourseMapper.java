package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.CourseResponseDto;
import org.diplom_backend.dto.responses.CourseShortResponseDto;
import org.diplom_backend.dto.responses.SchoolResponseDto;
import org.diplom_backend.model.CourseEntity;
import org.diplom_backend.model.School;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {CourseLessonMapper.class})
public interface CourseMapper {

    @Mapping(target = "schools", source = "schools", qualifiedByName = "schoolsToDto")
    @Mapping(target = "lessons", source = "lessons")
    @Mapping(target = "forLaggingStudents", source = "forLaggingStudents")
    @Mapping(target = "isIntroduction", source = "isIntroduction")
    CourseResponseDto toResponseDto(CourseEntity course);

    /**
     * Явная сборка DTO: иначе после добавления полей в record устаревший CourseMapperImpl
     * даёт NoSuchMethodError до clean-compile.
     */
    default CourseShortResponseDto toShortResponseDto(CourseEntity course) {
        if (course == null) {
            return null;
        }
        return new CourseShortResponseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                schoolsToDto(course.getSchools()),
                course.getIsActive(),
                course.getForLaggingStudents(),
                course.getIsIntroduction(),
                lessonsSize(course),
                course.getCreatedAt()
        );
    }

    @Named("lessonsSize")
    static Integer lessonsSize(CourseEntity course) {
        return course.getLessons() == null ? 0 : course.getLessons().size();
    }

    @Named("schoolsToDto")
    static List<SchoolResponseDto> schoolsToDto(Set<School> schools) {
        if (schools == null) return List.of();
        return schools.stream()
                .map(s -> new SchoolResponseDto(s.getId(), s.getName(), s.getTopicDeadline()))
                .toList();
    }
}