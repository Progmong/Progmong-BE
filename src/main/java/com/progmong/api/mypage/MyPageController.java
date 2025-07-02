package com.progmong.api.mypage;

import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageFacadeService mypageFacadeService;

    /**
     * 마이페이지 통합 정보 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<MyPageDto>> getMyPage(
            @AuthenticationPrincipal SecurityUser user) {
        MyPageDto responseDto = mypageFacadeService.getMyPageData(user.getId());
        return ApiResponse.success(SuccessStatus.MYPAGE_SUCCESS, responseDto);
    }
}
