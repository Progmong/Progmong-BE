package com.progmong.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    // 공통 성공 메시지
    OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),

    // 커스텀 성공 메시지 예시
    USER_REGISTERED(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    PET_STATUS_UPDATED(HttpStatus.OK, "펫 상태가 변경되었습니다."),
    PET_REGISTERED(HttpStatus.CREATED, "펫 등록이 완료되었습니다."),
    POST_DELETED(HttpStatus.OK, "게시글이 삭제되었습니다."),
    INTEREST_TAG_FOUND(HttpStatus.OK, "관심 태그 조회 성공"),
    INTEREST_TAG_UPDATED(HttpStatus.OK,"관심 태그가 성공적으로 수정되었습니다."),
    EXPLORE_START(HttpStatus.OK, "탐험 시작 성공"),
    GET_ALL_RECORD(HttpStatus.OK, "모든 사냥 기록 조회에 성공했습니다."),
    GET_PAGED_RECORD(HttpStatus.OK, "페이지네이션이 적용된 사냥 기록 조회에 성공했습니다."),
    GET_ALL_RECORD_COUNT(HttpStatus.OK, "모든 사냔 기록의 갯수 조회를 성공했습니다." ),
    EXPLORE_GET(HttpStatus.OK,"탐험 조회 성공"),
    EXPLORE_PROBLEM_SUCCESS(HttpStatus.OK, "탐험 문제 풀이 성공");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
