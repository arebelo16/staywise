package com.domiledge.dto;

import com.domiledge.model.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}