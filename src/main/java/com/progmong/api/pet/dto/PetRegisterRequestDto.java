package com.progmong.api.pet.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PetRegisterRequestDto {
    private Long userId;
    private Long petId;
    private String nickname;
}
