package com.progmong.api.user.dto;

import lombok.Getter;

@Getter
public class PasswordResetConfirmDto {
    private String code;
    private String newPassword;
}
