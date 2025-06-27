package com.progmong.api.pet.repository;

import com.progmong.api.pet.entity.UserPet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPetRepository extends JpaRepository<UserPet,Long> {
    Optional<UserPet> findByUserId(Long userId);
    boolean existsByNickname(String nickname);
}
