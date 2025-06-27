package com.progmong.api.pet.repository;

import com.progmong.api.pet.entity.UserPet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    Optional<UserPet> findByUserId(Long userId);

    boolean existsByNickname(String nickname);
}
