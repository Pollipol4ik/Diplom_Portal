package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.IdeaBankEntryResponseDto;
import org.diplom_backend.model.IdeaBankEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IdeaBankMapper {

    @Mapping(target = "createdByNickname", source = "createdBy.nickname")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    IdeaBankEntryResponseDto toResponseDto(IdeaBankEntry entry);
}