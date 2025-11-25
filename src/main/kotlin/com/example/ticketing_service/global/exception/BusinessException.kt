package com.example.ticketing_service.global.exception

open class BusinessException (
    val errorCode : ErrorCode
) : RuntimeException(errorCode.message)