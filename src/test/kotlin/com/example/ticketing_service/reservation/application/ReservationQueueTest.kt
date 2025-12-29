package com.example.ticketing_service.reservation.application

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.doAnswer
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(properties = [
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
])
class ReservationQueuePerformanceTest {

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @MockitoBean
    private lateinit var reservationCancelService: ReservationCancelService

    private val QUEUE_NAME = "reservation_cancel_queue"

    @BeforeEach
    fun setUp() {
        val blockingQueue = redissonClient.getBlockingQueue<Long>(QUEUE_NAME)
        blockingQueue.clear()
    }

    @Test
    @DisplayName("성능 테스트: 스레드 풀(5개) 적용 시 단일 스레드보다 약 5배 빨라야 한다")
    fun performanceTest() {
        val taskCount = 50
        val taskLatency = 100L

        val latch = CountDownLatch(taskCount)

        doAnswer {
            try {
                Thread.sleep(taskLatency)
                println("[${Thread.currentThread().name}] 작업 처리 완료")
            } finally {
                latch.countDown()
            }
            Unit
        }.`when`(reservationCancelService).cancelReservation(anyLong())

        val blockingQueue = redissonClient.getBlockingQueue<Long>(QUEUE_NAME)
        println("=== 작업 ${taskCount}개 큐에 투입 시작 ===")
        for (i in 1..taskCount) {
            blockingQueue.offer(i.toLong())
        }

        val startTime = System.currentTimeMillis()

        // 10초 대기
        val completed = latch.await(10, TimeUnit.SECONDS)

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        println("=========================================")
        println("총 작업 수: ${taskCount}개")
        println("작업당 지연: ${taskLatency}ms")
        println("-----------------------------------------")
        println("[이론적 소요 시간]")
        println(" - 단일 스레드일 때: ${taskCount * taskLatency} ms")
        println(" - 5개 스레드일 때: ${(taskCount * taskLatency) / 5} ms")
        println("-----------------------------------------")
        println("[실제 소요 시간]: ${totalTime}ms")
        println("=========================================")

        assertTrue(completed, "제한 시간 내에 작업을 완료하지 못했습니다.")

        assertTrue(totalTime < 2500, "스레드 풀 성능 미달: ${totalTime}ms 소요됨")
    }
}