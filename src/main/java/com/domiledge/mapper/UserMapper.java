package com.domiledge.mapper;

import com.domiledge.dto.UserDto;
import com.domiledge.dto.UserResponseDto;
import com.domiledge.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "password")
    User toEntity(UserDto dto);

    UserResponseDto toDto(User user);
}
