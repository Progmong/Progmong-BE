package com.progmong.api.user.dto;

import lombok.Getter;

@Getter
public class UserRegisterRequestDto {
    private String email;
    private String bojId;
    private String password;
    private String nickname;
}
