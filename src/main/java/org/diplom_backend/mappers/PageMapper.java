package org.diplom_backend.mappers;


import org.diplom_backend.dto.responses.PageResponseDto;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public interface PageMapper {
    <T, R> PageResponseDto<T> toPageResponseDto(Page<R> page, Function<R, T> mapper);

}
