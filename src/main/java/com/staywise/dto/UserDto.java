package com.staywise.dto;

import com.staywise.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String password;
    private Role role;
}
