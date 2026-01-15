package com.example.ticketing_service.queue

import com.example.ticketing_service.queue.application.WaitingQueueService
import com.example.ticketing_service.queue.scheduler.WaitingQueueScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
class WaitingQueueIntegrationTest {

    @Autowired lateinit var waitingQueueService: WaitingQueueService
    @Autowired lateinit var queueScheduler: WaitingQueueScheduler
    @Autowired lateinit var redisTemplate: StringRedisTemplate

    // 테스트가 끝나면 Redis 데이터 비우기
    @AfterEach
    fun tearDown() {
        redisTemplate.connectionFactory?.connection?.flushAll()
    }

    @Test
    @DisplayName("대기열 흐름 테스트: 대기 등록 -> 스케줄러 실행 -> 입장 성공")
    fun testQueueFlow() {
        val userId = "test_user_1"

        // 대기열 등록
        waitingQueueService.registerQueue(userId)

        // 검증 1: 대기열(ZSet)에는 존재하지만 아직 입장권(Active Token)은 없어야 함
        assertThat(waitingQueueService.getRank(userId)).isNotNull()
        assertThat(waitingQueueService.isAllowed(userId)).isFalse()

        // 스케줄러 강제 실행 (1초 기다리는 대신 메서드 직접 호출)
        queueScheduler.enterUser()

        // 검증 2: 대기열에서 사라지고, 입장권이 생겨야 함 (입장 성공)
        assertThat(waitingQueueService.getRank(userId)).isNull()
        assertThat(waitingQueueService.isAllowed(userId)).isTrue()
    }

    @Test
    @DisplayName("대기열 제한 테스트: 50명 입장 후 51번째는 대기해야 한다")
    fun testQueueLimit() {
        // 51명 등록
        for (i in 1..51) {
            waitingQueueService.registerQueue("user_$i")
        }

        // 스케줄러 실행 (50명만 입장 가능)
        queueScheduler.enterUser()

        // 검증
        assertThat(waitingQueueService.isAllowed("user_1")).isTrue()   // 1등은 입장
        assertThat(waitingQueueService.isAllowed("user_50")).isTrue()  // 50등도 입장
        assertThat(waitingQueueService.isAllowed("user_51")).isFalse() // 51등은 아직 대기 중
    }
}