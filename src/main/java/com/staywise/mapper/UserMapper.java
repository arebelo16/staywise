package com.staywise.mapper;

import com.staywise.dto.UserDto;
import com.staywise.dto.UserResponseDto;
import com.staywise.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "password")
    User toEntity(UserDto dto);

    UserResponseDto toDto(User user);
}
