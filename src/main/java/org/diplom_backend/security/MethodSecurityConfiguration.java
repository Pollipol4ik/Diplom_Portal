package org.diplom_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;

/**
 * Подключает {@link RoleHierarchy} к @PreAuthorize, чтобы ROLE_MODERATOR подразумевал ROLE_USER
 * (и не было 403 на эндпоинтах с @IsUser). Включение prePost — в {@link SecurityConfig}.
 */
@Configuration
public class MethodSecurityConfiguration {

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
