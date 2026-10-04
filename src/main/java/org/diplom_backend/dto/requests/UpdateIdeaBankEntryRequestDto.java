package org.diplom_backend.dto.requests;

import jakarta.validation.constraints.Size;

public record UpdateIdeaBankEntryRequestDto(

        @Size(max = 200)
        String title,

        @Size(max = 2000)
        String description,

        @Size(max = 5000)
        String comments,

        Integer score
) {}
