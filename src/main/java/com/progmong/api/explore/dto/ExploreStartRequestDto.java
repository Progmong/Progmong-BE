package com.progmong.api.explore.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExploreStartRequestDto {
    private int minLevel;
    private int maxLevel;
}