package org.shelter.app.mapper;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.shelter.app.database.entity.User;
import org.shelter.app.dto.UserCreateDto;
import org.shelter.app.dto.UserReadDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "password", expression = "java(passwordEncoder.encode(userCreateEditDto.getPassword()))"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "phone", ignore = true),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "birthDate", ignore = true),
            @Mapping(target = "profilePicture", ignore = true)
    })
    User toEntity(UserCreateDto userCreateEditDto, @Context PasswordEncoder passwordEncoder);

    @Named("mapMultipartFileToString")
    default String mapMultipartFileToString(MultipartFile file) {
        return file != null ? file.getOriginalFilename() : null;
    }

    UserReadDto toDto(User user);
}
