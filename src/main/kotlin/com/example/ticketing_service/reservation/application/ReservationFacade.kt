package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.common.RedisLockRepository
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class ReservationFacade(
    // private val redissonClient: RedissonClient,
    private val redisLockRepository: RedisLockRepository,
    private val reservationService: ReservationService
) {

    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        val lockKey = "lock:seat:${command.seatId}"
        val lockToken = UUID.randomUUID().toString() // 락 소유권 증명용 토큰

        // 스핀 락 설정
        val maxWaitTime = 3000L   // 3초 대기
        val leaseTime = 5000L     // 5초 뒤 자동 잠금 해제 (TTL)
        val startTime = System.currentTimeMillis()

        try {
            // 스핀 락 : 락 획득 시도 반복
            while (!redisLockRepository.lock(lockKey, lockToken, leaseTime)) {
                // 대기 시간 초과 체크
                if (System.currentTimeMillis() - startTime > maxWaitTime) {
                    throw BusinessException(ErrorCode.COMMON_LOCK_FAIL)
                }
                // 부하 감소를 위한 대기 시간 (너무 길면 실패함)
                Thread.sleep(20)
            }

            return reservationService.reserveSeat(command)

        } catch (e: InterruptedException) {
            throw BusinessException(ErrorCode.COMMON_SYSTEM_ERROR)
        } finally {
            // 토큰이 일치해야 락 해제 가능
            redisLockRepository.unlock(lockKey, lockToken)
        }
    }

//    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
//        val lockKey = "lock:seat:${command.seatId}"
//        val lock = redissonClient.getLock(lockKey)
//
//        try {
//            // 락 획득 시도 (대기 시간 3초/ 점유 시간 5초)
//            // 대기시간: 락을 얻기 위해 기다리는 시간 / 3초 안에 못 얻으면 포기
//            // 점유시간: 락을 얻고 나서 5초가 지나면 자동으로 풀림 / Deadlock 방지
//            val available = lock.tryLock(3, 5, TimeUnit.SECONDS)
//
//            if (!available) {
//                // 락 획득 실패
//                throw BusinessException(ErrorCode.COMMON_LOCK_FAIL)
//            }
//
//            return reservationService.reserveSeat(command)
//
//        } catch (e: InterruptedException) {
//            throw BusinessException(ErrorCode.COMMON_SYSTEM_ERROR)
//        } finally {
//            // 반드시 락 해제
//            if (lock.isHeldByCurrentThread) {
//                lock.unlock()
//            }
//        }
//    }
}