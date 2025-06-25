package com.progmong.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private long id;
    private String bojId;
    private String email;
    private String nickname;
}
