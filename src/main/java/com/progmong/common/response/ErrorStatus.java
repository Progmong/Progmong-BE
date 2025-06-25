package com.progmong.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorStatus {

    // 400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다."),
    MISSING_EMAIL_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST,"이메일 인증을 진행해주세요."),
    ALREADY_REGISTER_EMAIL_EXCEPETION(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    VALIDATION_EMAIL_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST,"올바른 이메일 형식이 아닙니다."),
    INVALID_PASSWORD_RESET_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"유효하지 않은 비밀번호 초기화 인증코드 입니다."),
    WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"이메일 인증코드가 올바르지 않습니다."),
    ALREADY_REGISTERED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    ALREADY_REGISTERED_PET(HttpStatus.BAD_REQUEST, "이미 등록된 펫이 있습니다."),


    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    EXPIRED_PASSWORD_RESET_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"비밀번호 초기화 인증코드가 만료되었습니다, 재인증 해주세요."),
    UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"이메일 인증코드가 만료되었습니다, 재인증 해주세요."),


    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404 NOT_FOUND
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 글을 찾을 수 없습니다."),
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫 정보를 찾을 수 없습니다."),
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다."),
    USER_PET_NOT_FOUND(HttpStatus.NOT_FOUND,"유저의 펫을 찾을 수 없습니다."),
    USER_INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND,"유저의 태그 정보가 없습니다."),
    RECOMMEND_PROBLEM_IN_BATTLE_NOT_FOUND(HttpStatus.NOT_FOUND, "전투 중인 추천 문제를 찾을 수 없습니다."),
    NEXT_RECOMMEND_PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "다음 추천 문제를 찾을 수 없습니다."),


    // 409 CONFLICT
    CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }

}
