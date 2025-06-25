package com.progmong.api.tag.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class InterestTagUpdateRequestDto {
    private List<Long> tagIds;  // 최종적으로 선택된 tagId 목록
}
