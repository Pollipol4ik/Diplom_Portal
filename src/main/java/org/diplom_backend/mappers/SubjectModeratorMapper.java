package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.SubjectModeratorResponseDto;
import org.diplom_backend.model.SubjectModerator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectModeratorMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "nickname", source = "account.nickname")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "assignedAt", source = "assignedAt")
    SubjectModeratorResponseDto toDto(SubjectModerator entity);

    List<SubjectModeratorResponseDto> toDtoList(List<SubjectModerator> entities);
}