package com.example.demo1.dtos;

import com.example.demo1.enums.UserRole;

public record SignUpDto(String login,
                        String password,
                        UserRole role) {
}
