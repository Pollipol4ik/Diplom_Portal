package org.diplom_backend.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "telegram.bot", ignoreUnknownFields = false)
public record AppConfig(
        @NotEmpty
        String telegramToken
) {
}
