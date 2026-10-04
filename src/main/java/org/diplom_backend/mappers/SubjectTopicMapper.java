package org.diplom_backend.mappers;

import org.diplom_backend.dto.requests.SubjectTopicRequestDto;
import org.diplom_backend.dto.responses.SubjectTopicResponseDto;
import org.diplom_backend.model.SubjectEntity;
import org.diplom_backend.model.SubjectTopicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface SubjectTopicMapper {

    @Named("getSubjectWithId")
    static SubjectEntity getSubjectWithId(Long id) {
        if (id == null) return null;
        var subject = new SubjectEntity();
        subject.setId(id);
        return subject;
    }

    @Mapping(target = "subjectResponseDTO", source = "subject")
    SubjectTopicResponseDto getSubjectTopicResponseDTO(SubjectTopicEntity subjectTopic);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", source = "subjectId", qualifiedByName = "getSubjectWithId")
    @Mapping(target = "name", source = "name")
    SubjectTopicEntity getSubjectTopicFromDTO(SubjectTopicRequestDto dto);
}