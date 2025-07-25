package com.progmong.api.user.entity;

import com.progmong.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String code;
    private Integer expirationTimeInMinutes;

    @Builder.Default
    private boolean isVerified = false;

    public boolean isExpired(LocalDateTime verifiedAt) {
        return verifiedAt.isAfter(this.createdAt.plusMinutes(this.expirationTimeInMinutes));
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String generateCodeMessage() {
        String formattedExpiredAt = this.createdAt.plusMinutes(this.expirationTimeInMinutes)
                .format(DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss")));
        return String.format(
                """
                        [프로그몽 이메일 인증]
                        인증코드: %s
                        
                        위의 인증코드로 이메일 인증을 진행해주세요.
                        코드 만료시간: %s""",
                this.code, formattedExpiredAt
        );
    }
}
