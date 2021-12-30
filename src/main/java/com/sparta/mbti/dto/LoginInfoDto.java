package com.sparta.mbti.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginInfoDto {
    private String name;
    private String token;

    @Builder
    public LoginInfoDto(String name, String token) {
        this.name = name;
        this.token = token;
    }
}