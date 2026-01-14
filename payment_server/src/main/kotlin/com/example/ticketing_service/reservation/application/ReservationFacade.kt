package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.common.RedisLockRepository
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReservationFacade(
    private val redisLockRepository: RedisLockRepository,
    private val reservationService: ReservationService,
    private val reservationQueueFacade: ReservationQueueFacade
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        val lockKey = "lock:seat:${command.seatId}"
        val lockToken = UUID.randomUUID().toString()

        try {
            if (!spinLock(lockKey, lockToken)) {
                throw BusinessException(ErrorCode.COMMON_LOCK_FAIL)
            }

            val response = reservationService.reserveSeat(command)

            log.info("[Facade] 예약 성공 (ReservationID: {}, SeatID: {})", response.reservationId, command.seatId)

            try {
                reservationQueueFacade.addToDelayQueue(response.reservationId)
            } catch (e: Exception) {
                // 큐 등록 실패 시 -> 방금 만든 예약 강제 취소 (Rollback)
                log.error("Redis 큐 등록 실패로 인한 보상 트랜잭션 실행. ID: ${response.reservationId}", e)

                try {
                    reservationService.cancelReservation(response.reservationId)
                } catch (rollbackEx: Exception) {
                    // 롤백까지 실패한 경우  -> 추후 배로나 로그 모니터링으로 처리 필요
                    log.error("CRITICAL: 예약 롤백 실패! 데이터 불일치 발생 가능. ID: ${response.reservationId}", rollbackEx)
                }

                throw e
            }

            return response

        }
        finally {
            redisLockRepository.unlock(lockKey, lockToken)
        }
    }

    private fun spinLock(key: String, token: String): Boolean {
        val maxWaitTime = 3000L
        val leaseTime = 5000L
        val startTime = System.currentTimeMillis()

        while (!redisLockRepository.lock(key, token, leaseTime)) {
            if (System.currentTimeMillis() - startTime > maxWaitTime) {
                return false
            }
            try {
                Thread.sleep(20)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            }
        }
        return true
    }
}