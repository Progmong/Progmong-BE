package com.progmong.api.pet.service;

import com.progmong.api.pet.dto.PetRegisterRequestDto;
import com.progmong.api.pet.dto.UserPetFullDto;
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
    public void registerPet(PetRegisterRequestDto request, Long userId){
        if(userPetRepository.findByUserId(userId).isPresent()){
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_PET.getMessage());
        }

        // 닉네임 중복 체크 추가
        String nickname = request.getNickname().trim();
        if (userPetRepository.existsByNickname(nickname)) {
            throw new BadRequestException("이미 존재하는 닉네임입니다.");
            // 또는 ErrorStatus에 새 에러 메시지 추가해서 활용
        }

        User user = userRepository.findById(userId)
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

    public UserPetFullDto getUserPetFullInfo(Long userId) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 펫 정보가 없습니다."));
        return new UserPetFullDto(userPet);
    }

}
