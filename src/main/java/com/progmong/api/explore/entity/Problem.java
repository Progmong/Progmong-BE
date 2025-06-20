package com.progmong.api.explore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
