package com.example.ticketing_service.reservation.application

import jakarta.annotation.PostConstruct
import org.redisson.api.RBlockingQueue
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Component
class ReservationQueueFacade(
    private val redissonClient: RedissonClient,
    private val reservationCancelService: ReservationCancelService
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val QUEUE_NAME = "reservation_cancel_queue"
    private val consumerThreadPool : ExecutorService = Executors.newFixedThreadPool(5)

    fun addToDelayQueue(reservationId: Long) {
        val blockingQueue: RBlockingQueue<Long> = redissonClient.getBlockingQueue(QUEUE_NAME)
        val delayedQueue = redissonClient.getDelayedQueue(blockingQueue)

        delayedQueue.offer(reservationId, 5, TimeUnit.MINUTES)

        log.info("[Queue] 예약({}) - 자동 취소 대기열 등록 완료", reservationId)
    }

    @PostConstruct
    fun startConsumer() {
        repeat(5) { threadId ->
            consumerThreadPool.submit {
                val blockingQueue = redissonClient.getBlockingQueue<Long>(QUEUE_NAME)
                log.info("[Consumer-$threadId] 시작됨")

                while (!Thread.currentThread().isInterrupted) {
                    try {
                        val reservationId = blockingQueue.take()
                        reservationCancelService.cancelReservation(reservationId)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    } catch (e: Exception) {
                        log.error("에러 발생", e)
                    }
                }
            }
        }
    }
}