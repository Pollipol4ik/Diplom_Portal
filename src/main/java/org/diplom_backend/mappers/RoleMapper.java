package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.RoleResponseDto;
import org.diplom_backend.model.Role;
import org.diplom_backend.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Named("getRoleAsString")
    static String getRole(Role role) {
        return role.getDescription();
    }

    @Named("roleCode")
    static String roleCode(Role role) {
        return role.name();
    }

    @Mapping(source = "name", target = "label", qualifiedByName = "getRoleAsString")
    @Mapping(source = "name", target = "role", qualifiedByName = "getRoleAsString")
    @Mapping(source = "name", target = "name", qualifiedByName = "getRoleAsString")
    @Mapping(source = "name", target = "code", qualifiedByName = "roleCode")
    RoleResponseDto toRoleResponseDto(RoleEntity roleEntity);
}
