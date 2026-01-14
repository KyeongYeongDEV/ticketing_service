package com.example.ticketing_service.queue.application

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WaitingQueueService(
    private val redisTemplate: StringRedisTemplate
) {
    private val WAITING_KEY = "queue:waiting"
    private val ACTIVE_KEY_PREFIX = "queue:active:"

    // 대기열 등록 (유저가 처음 접속했을 때 호출)
    fun registerQueue(userId: String): Long? {
        val timestamp = System.currentTimeMillis().toDouble()
        // Redis ZSet에 (Score=시간, Value=userId)로 저장
        redisTemplate.opsForZSet().add(WAITING_KEY, userId, timestamp)

        return getRank(userId)
    }

    // 내 대기 순번 확인 (프론트엔드에서 주기적으로 호출)
    fun getRank(userId: String): Long? {
        return redisTemplate.opsForZSet().rank(WAITING_KEY, userId)
    }

    // 입장 가능 여부 검증 (Interceptor에서 사용)
    fun isAllowed(userId: String): Boolean {
        // active 토큰이 존재하는지 확인
        return redisTemplate.hasKey("$ACTIVE_KEY_PREFIX$userId")
    }
}