package com.example.demo1.dtos;

import lombok.Getter;

@Getter
public class UserDto {
    private String login;
    private String role;

    public UserDto(String login, String role) {
        this.login = login;
        this.role = role;
    }
}
