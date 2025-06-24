package com.progmong.api.tag.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class InterestTagUpdateRequestDto {
    private Long userId;        //jwt 구현 후 삭제
    private List<Long> tagIds;  // 최종적으로 선택된 tagId 목록
}
