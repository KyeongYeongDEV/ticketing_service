package com.example.ticketing_service.queue.scheduler

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class WaitingQueueScheduler(
    private val redisTemplate: StringRedisTemplate
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val WAITING_KEY = "queue:waiting"
    private val ACTIVE_KEY_PREFIX = "queue:active:"

    // 한 번에 입장시킬 인원 수 50명 (트래픽에 따라 조절)
    private val ENTER_LIMIT = 50L

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    fun enterUser() {
        // 대기열에서 가장 오래 기다린 상위 ENTER_LIMIT 명 조회
        val usersToEnter = redisTemplate.opsForZSet().range(WAITING_KEY, 0, ENTER_LIMIT - 1)

        if (usersToEnter.isNullOrEmpty()) return

        log.info("입장 처리 시작: ${usersToEnter.size}명 진입 허용")

        usersToEnter.forEach { userId ->
            // 활성 토큰 발급 (유효시간 5분) -> 이제부터 API 호출 가능
            redisTemplate.opsForValue().set("$ACTIVE_KEY_PREFIX$userId", "ACCESS", Duration.ofMinutes(5))

            // 대기열(Waiting Queue)에서 제거
            redisTemplate.opsForZSet().remove(WAITING_KEY, userId)
        }
    }
}