package org.diplom_backend.mappers;

import org.diplom_backend.dto.requests.SchoolClassRequestDto;
import org.diplom_backend.dto.responses.SchoolClassResponseDto;
import org.diplom_backend.model.SchoolClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolClassMapper {

    @Mapping(source = "school.id", target = "schoolId")
    SchoolClassResponseDto toResponseDto(SchoolClass schoolClass);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true) // Школу установим в сервисе по ID
    SchoolClass toEntity(SchoolClassRequestDto dto);
}