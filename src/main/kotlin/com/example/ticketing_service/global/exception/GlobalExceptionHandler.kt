package com.example.ticketing_service.global.exception

import com.example.ticketing_service.global.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

import org.slf4j.LoggerFactory

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        val response = ApiResponse.error(e.errorCode)

        return ResponseEntity
            .status(e.errorCode.status)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e : Exception) : ResponseEntity<ApiResponse<Unit>> {
        logger.error("예상치 못한 에러 발생: ${e.message}", e)
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        return ResponseEntity
            .status(errorCode.status)
            .body(ApiResponse.error(errorCode))
    }
}