package org.diplom_backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роль системы")
public record RoleResponseDto(
        Long id,

        @Schema(description = "Подпись для UI (русское название)")
        String label,

        @Schema(description = "Дубликат label для совместимости со старыми клиентами")
        String role,

        @Schema(description = "Часто ожидаемое поле name в выпадающих списках")
        String name,

        @Schema(description = "ROLE_ADMIN | ROLE_MODERATOR | ROLE_USER")
        @JsonProperty("code")
        String code
) {
}
