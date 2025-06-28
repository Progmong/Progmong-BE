package com.progmong.api.tag.dto;


import java.util.List;
import lombok.Getter;

@Getter
public class InterestTagUpdateRequestDto {
    private List<Long> tagIds;  // 최종적으로 선택된 tagId 목록
}
