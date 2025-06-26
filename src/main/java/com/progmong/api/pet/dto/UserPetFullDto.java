package com.progmong.api.pet.dto;


import com.progmong.api.pet.entity.Pet;
import com.progmong.api.pet.entity.PetStatus;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPetFullDto {

    private Long userPetId;
    private Long  userId;
    private Long  petId;
    private int level;
    private int currentExp;
    private int maxExp;
    private PetStatus status;
    private String message;
    private boolean isProud;
    private String nickname;
    private int evolutionStage;

    public UserPetFullDto(UserPet userPet) {
        this.userPetId = userPet.getId();
        this.userId = userPet.getUser().getId();
        this.petId = userPet.getPet().getId();
        this.level = userPet.getLevel();
        this.currentExp = userPet.getCurrentExp();
        this.maxExp = userPet.getMaxExp();
        this.status = userPet.getStatus();
        this.message = userPet.getMessage();
        this.isProud = userPet.isProud(); // boolean은 is~ 메서드로 받아야 함
        this.nickname = userPet.getNickname();
        this.evolutionStage = userPet.getEvolutionStage();
    }
}
