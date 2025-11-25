package com.example.ticketing_service.global.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e : BusinessException) : ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        val response = ErrorResponse.of(errorCode)

        return ResponseEntity
            .status(errorCode.status)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e : BusinessException) : ResponseEntity<ErrorResponse> {
        e.printStackTrace() //로그 남기기
        val errorCode = e.errorCode
        val response = ErrorResponse.of(errorCode)

        return ResponseEntity
            .status(errorCode.status)
            .body(response)
    }
}