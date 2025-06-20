package com.progmong.api.explore.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ExpRule
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