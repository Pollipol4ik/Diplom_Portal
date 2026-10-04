package org.diplom_backend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubjectByDirectionIdRequestDto extends PageRequestDto {
    @Min(1)
    @NotNull
    @Schema(description = "ID направления", example = "1")
    private Long directionId;
}