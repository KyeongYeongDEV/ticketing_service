package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ReservationFacade(
    private val redissonClient: RedissonClient,
    private val reservationService: ReservationService
) {

    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        val lockKey = "lock:seat:${command.seatId}"
        val lock = redissonClient.getLock(lockKey)

        try {
            // 락 획득 시도 (대기 시간 3초/ 점유 시간 5초)
            // 대기시간: 락을 얻기 위해 기다리는 시간 / 3초 안에 못 얻으면 포기
            // 점유시간: 락을 얻고 나서 5초가 지나면 자동으로 풀림 / Deadlock 방지
            val available = lock.tryLock(3, 5, TimeUnit.SECONDS)

            if (!available) {
                // 락 획득 실패
                throw BusinessException(ErrorCode.COMMON_LOCK_FAIL)
            }

            return reservationService.reserveSeat(command)

        } catch (e: InterruptedException) {
            throw BusinessException(ErrorCode.COMMON_SYSTEM_ERROR)
        } finally {
            // 반드시 락 해제
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}