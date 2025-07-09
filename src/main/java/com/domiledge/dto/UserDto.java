package com.domiledge.dto;

import com.domiledge.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String password;
    private Role role;
}
