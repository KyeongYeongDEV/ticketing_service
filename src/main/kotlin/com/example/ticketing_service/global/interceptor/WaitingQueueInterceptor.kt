package com.example.ticketing_service.global.interceptor

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.queue.application.WaitingQueueService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class WaitingQueueInterceptor(
    private val waitingQueueService: WaitingQueueService
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Preflight 요청(OPTIONS)은 통과
        if (request.method == "OPTIONS") return true

        // 헤더에서 userId 추출 (실서비스에선 Authorization 토큰 파싱)
        val userId = request.getHeader("X-User-Id")

        if (userId.isNullOrBlank()) {
            // 유저 정보가 없으면 에러
            throw BusinessException(ErrorCode.UNAUTHORIZED_ACTION)
        }

        // 대기열 검증: 활성 토큰이 있는지 확인
        if (!waitingQueueService.isAllowed(userId)) {
            // 토큰이 없으면 403 에러
            throw BusinessException(ErrorCode.QUEUE_TOKEN_NOT_FOUND)
        }

        return true
    }
}