package com.example.ticketing_service.queue.scheduler

import com.example.ticketing_service.global.common.RedisLockRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

@Component
class WaitingQueueScheduler(
    private val redisTemplate: StringRedisTemplate,
    private val redisLockRepository: RedisLockRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val WAITING_KEY = "queue:waiting"
    private val ACTIVE_KEY_PREFIX = "queue:active:"
    private val ENTER_LIMIT = 50L

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    fun enterUser() {
        val lockKey = "scheduler:lock"
        val lockToken = UUID.randomUUID().toString()

        try {
            // 락 획득 시도 (3초간 유효) -> 실패하면 이번 턴은 스킵
            if (!redisLockRepository.lock(lockKey, lockToken, 3000)) {
                return
            }

            //대기열 입장
            val usersToEnter = redisTemplate.opsForZSet().range(WAITING_KEY, 0, ENTER_LIMIT - 1)

            if (!usersToEnter.isNullOrEmpty()) {
                log.info("입장 처리 시작: ${usersToEnter.size}명 진입 허용")

                usersToEnter.forEach { userId ->
                    // 활성 토큰 발급 (유효시간 5분)
                    redisTemplate.opsForValue().set("$ACTIVE_KEY_PREFIX$userId", "ACCESS", Duration.ofMinutes(5))

                    // 대기열 제거
                    redisTemplate.opsForZSet().remove(WAITING_KEY, userId)
                }
            }

        } catch (e: Exception) {
            log.error("스케줄러 실행 중 에러 발생", e)
        } finally {
            // 락 해제
            redisLockRepository.unlock(lockKey, lockToken)
        }
    }
}