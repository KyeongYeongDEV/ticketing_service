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

            reservationQueueFacade.addToDelayQueue(response.reservationId)

            return response

        } finally {
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