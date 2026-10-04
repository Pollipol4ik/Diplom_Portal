package org.diplom_backend.mappers;

import org.diplom_backend.dto.requests.SubjectRequestDto;
import org.diplom_backend.dto.responses.SubjectResponseDto;
import org.diplom_backend.model.DirectionEntity;
import org.diplom_backend.model.SubjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Маппер для предмета
 * Автор: Полина Купцова
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Named("getSubjectName")
    static String getSubjectName(String name) {
        return name != null ? name.trim().toUpperCase() : null;
    }

    /**
     * Создает прокси-объект направления с установленным ID для связи
     */
    @Named("directionWithId")
    static DirectionEntity directionWithId(Long directionId) {
        if (directionId == null) return null;
        DirectionEntity direction = new DirectionEntity();
        direction.setId(directionId);
        return direction;
    }

    /**
     * Конвертация из Entity в DTO-ответ
     */
    @Mapping(target = "name", source = "name")
    SubjectResponseDto toSubjectResponseDTO(SubjectEntity subject);

    /**
     * Конвертация из DTO-запроса в Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectTopics", ignore = true)
    @Mapping(target = "direction", qualifiedByName = "directionWithId", source = "directionId")
    @Mapping(target = "name", qualifiedByName = "getSubjectName", source = "name")
    SubjectEntity fromSubjectRequestDTO(SubjectRequestDto dto);
}