package com.progmong.api.mypage;

import com.progmong.api.explore.dto.RecommendProblemListResponseDto;
import com.progmong.api.explore.service.ExploreService;
import com.progmong.api.pet.dto.UserPetFullDto;
import com.progmong.api.pet.service.UserPetService;
import com.progmong.api.tag.dto.InterestTagResponseDto;
import com.progmong.api.tag.service.InterestTagService;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageFacadeService {

    private final UserService userService;
    private final UserPetService userPetService;
    private final ExploreService exploreService;
    private final InterestTagService tagService;

    public MyPageDto getMyPageData(Long userId) {
        // 1. 사용자 정보
        User user = userService.getUserById(userId);
        // 2. 펫 정보
        UserPetFullDto petInfo = userPetService.getUserPetFullInfo(userId);
        // 3. 최근 탐험 기록 (5개)
        RecommendProblemListResponseDto recentExplores = exploreService.getMyPageRecentExplore(userId);
        // 4. 관심 태그
        List<InterestTagResponseDto> tags = tagService.getUserInterestTags(userId);

        // 반환
        return MyPageDto.builder()
                .user(userService.toUserInfoResponseDto(user))
                .userPet(petInfo)
                .recentExplores(recentExplores)
                .interestTags(tags)
                .build();
    }
}
