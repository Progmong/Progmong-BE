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

    // 커스텀 성공 메시지
    USER_REGISTERED(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    USER_LOGGED_IN(HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),
    USER_DELETED(HttpStatus.OK, "회원 탈퇴가 완료되었습니다."),
    SEND_EMAIL_VERIFICATION_CODE_SUCCESS(HttpStatus.OK, "이메일 인증코드 발송 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 코드 인증 성공"),
    SEND_PASSWORD_RESET_CODE_SUCCESS(HttpStatus.OK, "비밀번호 초기화 코드 전송 성공"),
    SEND_UPDATE_USER_PASSWORD(HttpStatus.OK, "비밀번호 변경 성공"),
    GET_USER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 조회 성공"),
    PET_INFO_LOADED(HttpStatus.OK, "사용자 펫 정보 조회 성공"),
    PET_INFO_NOT_FOUND(HttpStatus.OK, "사용자 펫 정보가 없습니다."),

    PET_STATUS_UPDATED(HttpStatus.OK, "펫 상태가 변경되었습니다."),
    PET_REGISTERED(HttpStatus.CREATED, "펫 등록이 완료되었습니다."),
    INTEREST_TAG_FOUND(HttpStatus.OK, "관심 태그 조회 성공"),
    INTEREST_TAG_UPDATED(HttpStatus.OK, "관심 태그가 성공적으로 수정되었습니다."),
    EXPLORE_START(HttpStatus.OK, "탐험 시작 성공"),
    GET_ALL_RECORD(HttpStatus.OK, "모든 사냥 기록 조회에 성공했습니다."),
    GET_PAGED_RECORD(HttpStatus.OK, "페이지네이션이 적용된 사냥 기록 조회에 성공했습니다."),


    // 커뮤니티
    POST_WRITE_SUCCESS(HttpStatus.OK, "게시글 작성이 완료되었습니다."),
    POST_FIND_SUCCESS(HttpStatus.OK, "게시글 조회 성공"),
    POST_ALL_SUCCESS(HttpStatus.OK, "게시글 리스트 불러오기 성공"),
    POST_MODIFY_SUCCESS(HttpStatus.OK, "게시글 수정 성공"),
    POST_DELETED(HttpStatus.OK, "게시글이 삭제되었습니다."),
    COMMUNIY_ACTIVE_SUCCESS(HttpStatus.OK, "커뮤니티 활동 이력 불러오기 성공"),

    GET_ALL_RECORD_COUNT(HttpStatus.OK, "모든 사냔 기록의 갯수 조회를 성공했습니다."),
    EXPLORE_GET(HttpStatus.OK, "탐험 조회 성공"),
    EXPLORE_PROBLEM_SUCCESS(HttpStatus.OK, "탐험 문제 풀이 성공"),
    EXPLORE_CHECK_PROBLEM_SUCCESS(HttpStatus.OK, "문제 풀이 여부 조회 성공"),
    EXPLORE_PASS(HttpStatus.OK, "탐험 패스 성공"),
    PET_NICKNAME_UPDATED(HttpStatus.OK, "펫 닉네임이 변경되었습니다."),
    PET_MESSAGE_UPDATED(HttpStatus.OK, "펫 메시지가 변경되었습니다."),
    PET_PROUD_STATUS_LOADED(HttpStatus.OK, "펫 자랑 상태 조회 성공"),
    UPDATE_USER_NICKNAME_SUCCESS(HttpStatus.OK, "닉네임이 성공적으로 변경되었습니다."),

    // 댓글
    COMMENT_ALL_SUCCESS(HttpStatus.OK, "댓글 목록 조회 성공"),
    COMMENT_WRITE_SUCCESS(HttpStatus.OK, "댓글 작성 성공"),
    COMMENT_MODIFY_SUCCESS(HttpStatus.OK, "댓글 수정 성공"),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "댓글 삭제 성공");


    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
