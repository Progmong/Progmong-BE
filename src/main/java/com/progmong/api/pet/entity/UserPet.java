package com.progmong.api.pet.entity;

import com.progmong.api.user.entity.User;
import com.progmong.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


// UserPet
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ColumnDefault("1")
    private int level;

    @ColumnDefault("0")
    private int exp;

    @Enumerated(EnumType.STRING)
    private PetStatus status;

    private String message;
    private boolean isProud = false;
    private String nickname;

    @ColumnDefault("1")
    private int evolutionStage;

    public void updateStatus(PetStatus status) {
        this.status = status;
    }
}