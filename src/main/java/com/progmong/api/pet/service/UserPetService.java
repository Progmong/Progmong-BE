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

    public static final String DEFAULT_MESSAGE = "프로그몽! 만나서 반가워요!";
    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Transactional //여러 DB 작업이 엮일 경우
    public void registerPet(PetRegisterRequestDto request, Long userId) {
        if (userPetRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_PET.getMessage());
        }

        // 닉네임 중복 체크 추가
        String nickname = request.getNickname().trim();
        if (userPetRepository.existsByNickname(nickname)) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_PET_NICKNAME.getMessage());
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
                .message(DEFAULT_MESSAGE)
                .isProud(false)
                .nickname(request.getNickname())
                .evolutionStage(1)
                .build();

        userPetRepository.save(userPet);
    }

    public UserPetFullDto getUserPetFullInfo(Long userId) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PET_NOT_FOUND.getMessage()));
        return new UserPetFullDto(userPet);
    }


    // 펫 메시지 수정 메서드 추가
    @Transactional
    public void updatePetMessage(Long userId, String message) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));

        userPet.setMessage(message);
        userPetRepository.save(userPet);
    }

    // 펫 닉네임 수정 메서드 추가
    @Transactional
    public void updatePetNickname(String petNickName, Long userId) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));

        // 닉네임 중복 체크
        String newNickname = petNickName.trim();
        if (userPetRepository.existsByNickname(newNickname)) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_PET_NICKNAME.getMessage());
        }

        userPet.setNickname(newNickname);
        userPetRepository.save(userPet);
    }

    // 펫 공개 여부 변경 메서드 추가
    @Transactional
    public void togglePetVisibility(Long userId) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));

        userPet.setIsProud(!userPet.isProud()); // 현재 상태를 반전시킴
        userPetRepository.save(userPet);
    }

    public boolean isPetProud(Long userId) {
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));
        return userPet.isProud();
    }
}
