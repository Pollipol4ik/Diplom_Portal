package org.diplom_backend.mappers;

import org.diplom_backend.dto.requests.SchoolRequestDto;
import org.diplom_backend.dto.responses.SchoolResponseDto;
import org.diplom_backend.model.School;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    /**
     * Преобразование сущности в DTO для ответа клиенту
     */
    SchoolResponseDto toSchoolResponseDto(School school);

    /**
     * Преобразование входящих данных (Request) в сущность для сохранения.
     * Мы игнорируем id, так как он генерируется базой данных автоматически.
     */
    @Mapping(target = "id", ignore = true)
    School toEntity(SchoolRequestDto schoolRequestDto);
}