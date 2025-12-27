package com.example.ticketing_service.reservation.application

import jakarta.annotation.PostConstruct
import org.redisson.api.RBlockingQueue
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Component
class ReservationQueueFacade(
    private val redissonClient: RedissonClient,
    private val reservationCancelService: ReservationCancelService
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val QUEUE_NAME = "reservation_cancel_queue"

    fun addToDelayQueue(reservationId: Long) {
        val blockingQueue: RBlockingQueue<Long> = redissonClient.getBlockingQueue(QUEUE_NAME)
        val delayedQueue = redissonClient.getDelayedQueue(blockingQueue)

        delayedQueue.offer(reservationId, 5, TimeUnit.MINUTES)

        log.info("[Queue] 예약({}) - 자동 취소 대기열 등록 완료", reservationId)
    }

    @PostConstruct
    fun startConsumer() {
        thread(name = "Reservation-Delay-Consumer") {
            val blockingQueue: RBlockingQueue<Long> = redissonClient.getBlockingQueue(QUEUE_NAME)

            log.info("[System] 지연 큐 컨슈머 스레드 시작됨")

            while (true) {
                try {
                    val reservationId = blockingQueue.take()
                    reservationCancelService.cancelReservation(reservationId)

                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    log.warn("[System] 컨슈머 스레드 종료됨")
                    break
                } catch (e: Exception) {
                    log.error("[Error] 취소 처리 중 예외 발생", e)
                }
            }
        }
    }
}