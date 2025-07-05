package com.staywise.dto;

import com.staywise.model.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}