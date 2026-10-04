package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.DirectionResponseDto;
import org.diplom_backend.model.DirectionEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Маппер для работы с направлениями обучения (например, Инженерная школа)
 * Автор: Полина Купцова
 */
@Mapper(componentModel = "spring")
public interface DirectionMapper {

    /**
     * Конвертация сущности направления в ДТО
     *
     * @param direction сущность направления
     * @return ДТО направления
     */
    DirectionResponseDto toDirectionResponseDto(DirectionEntity direction);

    /**
     * Конвертация списка сущностей в список ДТО
     *
     * @param directions список направлений
     * @return список ДТО направлений
     */
    List<DirectionResponseDto> toDirectionResponseDtoList(List<DirectionEntity> directions);
}