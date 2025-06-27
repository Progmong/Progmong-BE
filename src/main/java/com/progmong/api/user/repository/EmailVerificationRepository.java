package com.progmong.api.user.repository;

import com.progmong.api.user.entity.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);

    Optional<EmailVerification> findByCode(String code);

}
