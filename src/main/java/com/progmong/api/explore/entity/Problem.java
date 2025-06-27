package com.progmong.api.explore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "problem")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @Column(name = "problem_id")
    private Long id;

    private String title;
    private int level;
    private String mainTag;
    private int solvedUserCount;

    private String mainTagKo;
}
