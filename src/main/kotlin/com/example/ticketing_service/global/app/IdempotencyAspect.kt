package com.example.ticketing_service.global.aop

import com.example.ticketing_service.global.annotation.Idempotent
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Duration

@Aspect
@Component
class IdempotencyAspect(
    private val redisTemplate: StringRedisTemplate
) {

    @Before("@annotation(idempotent)")
    fun checkIdempotency(idempotent: Idempotent) {
        val request: HttpServletRequest = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        // 헤더에서 키 추출
        val idempotencyKey = request.getHeader("Idempotency-Key")
        if (idempotencyKey.isNullOrBlank()) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }

        val redisKey = "idempotency:$idempotencyKey"

        // Redis에 키 저장 (SETNX: 없으면 저장하고 true, 있으면 false)
        // 유효 시간 10분 설정
        val isFirstRequest = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(10))

        // 이미 키가 존재하면(false) 중복 요청으로 간주하고 에러 발생
        if (isFirstRequest == false) {
            throw BusinessException(ErrorCode.DUPLICATE_REQUEST) // "이미 처리된 요청입니다."
        }
    }
}