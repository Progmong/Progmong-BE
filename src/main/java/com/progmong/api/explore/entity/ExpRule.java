package com.progmong.api.explore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ExpRule
@Builder
@Entity
@Table(name = "exp_rule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_rule_id")
    private Long id;

    private int minRating;
    private int maxRating;
    private int exp;
}
