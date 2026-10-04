package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.CourseLessonResponseDto;
import org.diplom_backend.model.CourseLessonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseLessonMapper {

    @Mapping(target = "lectureFileName", source = "lectureFile.initialFileName")
    @Mapping(target = "lectureFileNameInDirectory", source = "lectureFile.fileNameInDirectory")
    CourseLessonResponseDto toResponseDto(CourseLessonEntity lesson);
}
