package org.diplom_backend.mappers;


import org.diplom_backend.dto.requests.SaveInformationAboutAccountRequestDto;
import org.diplom_backend.dto.responses.AccountResponseDto;
import org.diplom_backend.dto.responses.AccountUpdateRoleResponseDto;
import org.diplom_backend.dto.responses.GetAllUserResponseDto;
import org.diplom_backend.dto.responses.StatusAccountResponseDto;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Named("getRole")
    static String getRole(RoleEntity role) {
        return role.getName().getDescription();
    }

    @Named("getDate")
    static String getDate(LocalDate dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    @Named("getBirthDate")
    static LocalDate getBirthDate(String s) {
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    @Mapping(target = "photoNameInDirectory", source = "profilePhoto.fileNameInDirectory")
    @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "getDate")
    @Mapping(source = "school.id", target = "schoolId")
    @Mapping(source = "school.name", target = "schoolName")
    @Mapping(source = "schoolClass.id", target = "classId")
    @Mapping(source = "schoolClass.name", target = "className")
    AccountResponseDto toAccountResponseDto(Account account);

    @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "profilePhoto.fileNameInDirectory", target = "photoNameInDirectory")
    @Mapping(source = "school.id", target = "schoolId")
    @Mapping(source = "school.name", target = "schoolName")
    @Mapping(source = "schoolClass.id", target = "classId")
    @Mapping(source = "schoolClass.name", target = "className")
    @Mapping(source = "createdAt", target = "createdAt")
    GetAllUserResponseDto toGetAllUserResponseDto(Account account);

    StatusAccountResponseDto toStatusAccountResponseDto(Account account);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "publications", ignore = true)
    @Mapping(target = "profilePhoto", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isBanned", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "getBirthDate")
    Account fromSaveInformationAboutAccountRequestDto(SaveInformationAboutAccountRequestDto saveInformationAboutAccountRequestDto);

    @Mapping(source = "role", target = "role", qualifiedByName = "getRole")
    AccountUpdateRoleResponseDto toAccountUpdateRoleResponseDto(Account account);
}