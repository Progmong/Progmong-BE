package com.progmong.api.pet.service;

import com.progmong.api.pet.dto.PetRegisterRequestDto;
import com.progmong.api.pet.entity.Pet;
import com.progmong.api.pet.entity.PetStatus;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.pet.repository.PetRepository;
import com.progmong.api.pet.repository.UserPetRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserPetService {

    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Transactional //여러 DB 작업이 엮일 경우
    public void registerPet(PetRegisterRequestDto request){
        if(userPetRepository.findByUserId(request.getUserId()).isPresent()){
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_PET.getMessage());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PET_NOT_FOUND.getMessage()));

        UserPet userPet = UserPet.builder()
                .user(user)
                .pet(pet)
                .level(1)
                .status(PetStatus.휴식)
                .message(null)
                .isProud(false)
                .nickname(request.getNickname())
                .evolutionStage(1)
                .build();

        userPetRepository.save(userPet);



    }
}
