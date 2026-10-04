package org.diplom_backend.mappers;


import org.diplom_backend.dto.responses.FileResponseDto;
import org.diplom_backend.model.FileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileResponseDto toFileResponseDto(FileEntity file);

}
