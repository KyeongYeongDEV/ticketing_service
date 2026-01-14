package com.example.ticketing_service.global.common.response

import com.example.ticketing_service.global.exception.ErrorCode

data class ApiResponse<T> (
    val result : ResultType,
    val data : T? = null,
    val message : String? = null,
    val errorCode : String? = null
){
    enum class ResultType {
        SUCCESS, ERROR
    }

    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                result = ResultType.SUCCESS,
                data = data,
                message = message
            )
        }

        // 데이터 없을 때
        fun success(message: String? = null): ApiResponse<Unit> {
            return ApiResponse(
                result = ResultType.SUCCESS,
                message = message,
                data = null
            )
        }

        fun error(errorCode : ErrorCode, message : String? = null) : ApiResponse<Unit> {
            return ApiResponse(
                result = ResultType.ERROR,
                message = message ?: errorCode.message,
                errorCode = errorCode.code
            )
        }
    }
}