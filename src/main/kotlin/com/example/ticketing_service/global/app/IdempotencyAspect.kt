package com.example.ticketing_service.global.aop

import com.example.ticketing_service.global.annotation.Idempotent
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
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

    @Around("@annotation(idempotent)")
    fun checkIdempotency(joinPoint: ProceedingJoinPoint, idempotent: Idempotent): Any? {
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

        // 이미 키가 존재하면 중복 요청으로 간주하고 에러 발생
        if (isFirstRequest == false) {
            throw BusinessException(ErrorCode.DUPLICATE_REQUEST)
        }

        // 비즈니스 로직 실행 및 예외 처리
        try {
            // 실제 컨트롤러 메서드 실행
            return joinPoint.proceed()
        } catch (e: Exception) {
            // 로직 실패 시 키 삭제
            redisTemplate.delete(redisKey)
            throw e
        }
    }
}