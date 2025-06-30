package com.progmong.api.mypage;

import com.progmong.api.explore.dto.RecommendProblemListResponseDto2;
import com.progmong.api.pet.dto.UserPetFullDto;
import com.progmong.api.tag.dto.InterestTagResponseDto;
import com.progmong.api.user.dto.UserInfoResponseDto;
import java.util.List;
import lombok.Builder;

@Builder
public class MyPageDto {
    UserInfoResponseDto user;
    UserPetFullDto userPet;
    RecommendProblemListResponseDto2 recentExplores; // 최근 탐험 기록 (5개)
    List<InterestTagResponseDto> interestTags;

}
