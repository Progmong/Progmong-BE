package com.progmong.api.pet.entity;

import com.progmong.api.user.entity.User;
import com.progmong.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Builder
@Entity
@Table(name = "user_pet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pet_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ColumnDefault("1")
    private int level;

    @ColumnDefault("0")
    private int currentExp;

    @ColumnDefault("50")
    private int maxExp;

    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @Setter
    private String message;

    @ColumnDefault("false")
    private boolean isProud;

    @Setter
    private String nickname;

    @ColumnDefault("1")
    private int evolutionStage;

    public void updateStatus(PetStatus status) {
        this.status = status;
    }

    public void addExp(int exp) {
        this.currentExp += exp;

        while (this.currentExp >= this.maxExp) {
            this.currentExp -= this.maxExp;
            this.level++;
            this.maxExp += 50;

            if (this.level == 6 || this.level == 11 || this.level == 16) {
                this.evolutionStage++;
            }
        }
    }

    public void setIsProud(boolean b) {
        this.isProud = b;
    }
}
