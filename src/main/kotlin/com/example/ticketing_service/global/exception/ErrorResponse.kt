package com.example.ticketing_service.global.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ErrorResponse(
    val timestamp : String = LocalDateTime.now().toString(),
    val code : String,
    val status : Int,
    val message : String
) {
    companion object{
        fun of(errorCode : ErrorCode) : ErrorResponse {
            return ErrorResponse(
                code = errorCode.code,
                status = errorCode.status.value(),
                message = errorCode.message
            )
        }
    }
}