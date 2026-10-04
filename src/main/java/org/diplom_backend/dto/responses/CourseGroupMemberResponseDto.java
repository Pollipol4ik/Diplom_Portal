package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Участник группы курса")
public record CourseGroupMemberResponseDto(

        @Schema(description = "ID записи")
        Long id,

        @Schema(description = "ID аккаунта")
        @JsonProperty("account_id")
        Long accountId,

        @Schema(description = "Никнейм")
        String nickname,

        @Schema(description = "Имя")
        @JsonProperty("first_name")
        String firstName,

        @Schema(description = "Фамилия")
        @JsonProperty("last_name")
        String lastName,

        @Schema(description = "Является ли владельцем группы")
        @JsonProperty("is_owner")
        Boolean isOwner
) {}
