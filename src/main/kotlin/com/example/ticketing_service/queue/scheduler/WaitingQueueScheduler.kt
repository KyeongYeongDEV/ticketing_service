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

    @Scheduled(fixedDelay = 1000)
    fun enterUser() {
        val lockKey = "scheduler:lock"
        val lockToken = UUID.randomUUID().toString()

        try {
            if (!redisLockRepository.lock(lockKey, lockToken, 3000)) return

            val usersToEnter = redisTemplate.opsForZSet().range(WAITING_KEY, 0, ENTER_LIMIT - 1)

            if (!usersToEnter.isNullOrEmpty()) {
                usersToEnter.forEach { userId ->
                    // 활성 토큰 발급
                    redisTemplate.opsForValue().set("$ACTIVE_KEY_PREFIX$userId", "ACCESS", Duration.ofMinutes(5))
                    // 대기열 제거
                    redisTemplate.opsForZSet().remove(WAITING_KEY, userId)

                    // Redis Pub/Sub으로 알림 발송
                    // "queue:entry" 채널에 userId를 던지면, 모든 서버의 리스너가 듣습니다.
                    redisTemplate.convertAndSend("queue:entry", userId)
                }
            }
        } catch (e: Exception) {
            log.error("스케줄러 에러", e)
        } finally {
            redisLockRepository.unlock(lockKey, lockToken)
        }
    }
}