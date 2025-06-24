package com.progmong.api.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterestTagResponseDto {       // interest_tag의 get에 사용
    private Long id;    // userId AuthenticationPrincipal 이후 교체 예정
    private String name;
}
