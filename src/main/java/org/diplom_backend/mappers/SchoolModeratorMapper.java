package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.SchoolModeratorResponseDto;
import org.diplom_backend.model.SchoolModerator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SchoolModeratorMapper {

    @Mapping(target = "accountId",  source = "account.id")
    @Mapping(target = "nickname",   source = "account.nickname")
    @Mapping(target = "schoolId",   source = "school.id")
    @Mapping(target = "schoolName", source = "school.name")
    SchoolModeratorResponseDto toResponseDto(SchoolModerator entity);

    List<SchoolModeratorResponseDto> toResponseDtoList(List<SchoolModerator> entities);
}