package com.example.ticketing_service.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode (
    val code : String,
    val status : HttpStatus,
    val message : String
){
    // Global
    INTERNAL_SERVER_ERROR("G001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),
    INVALID_INPUT_VALUE("G002", HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    UNAUTHORIZED_ACTION("G003", HttpStatus.FORBIDDEN, "권한이 없는 작업입니다."),

    // User
    USER_NOT_FOUND("US001", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION("US002", HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    // Concert/Seat
    SEAT_NOT_FOUND("SE001", HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    SEAT_ALREADY_RESERVED("SE002", HttpStatus.BAD_REQUEST, "이미 선택된 좌석입니다."),

    // Payment
    PAYMENT_FAILED("PM001", HttpStatus.INTERNAL_SERVER_ERROR, "결제 승인에 실패했습니다.");
}