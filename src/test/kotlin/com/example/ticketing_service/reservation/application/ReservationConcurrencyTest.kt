package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.seat.domain.SeatRepository
import com.example.ticketing_service.seat.domain.SeatStatus
import com.example.ticketing_service.support.TestDatabaseInitializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.PessimisticLockingFailureException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired private lateinit var reservationService: ReservationService
    @Autowired private lateinit var seatRepository: SeatRepository
    @Autowired private lateinit var reservationRepository: ReservationRepository
    @Autowired private lateinit var testDatabaseInitializer: TestDatabaseInitializer

    private val targetSeatId = 1L

    @BeforeEach
    fun setUp() {
        testDatabaseInitializer.clearAndInitialize()
    }

    @Test
    @DisplayName("비관적 락 적용: 100명이 동시에 예약을 시도하면 1명만 성공하고 나머지는 실패해야 한다")
    fun pessimisticLockTest() {
        // Given
        val numberOfThreads = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(numberOfThreads)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        // 비관적 락에서 주로 발생하는 두 가지 예외
        val businessExceptionCount = AtomicInteger(0) // 이미 예약됨
        val lockTimeoutCount = AtomicInteger(0)       // 락 획득 시간 초과

        val startTime = System.currentTimeMillis()

        // When
        for (i in 1..numberOfThreads) {
            executorService.submit {
                try {
                    // 실제 상황처럼 유저 ID를 다르게 부여 (1~100)
                    val command = ReserveSeatCommand(userId = i.toLong(), seatId = targetSeatId)
                    reservationService.reserveSeat(command)

                    successCount.getAndIncrement()
                } catch (e: Exception) {
                    failCount.getAndIncrement()

                    when (e) {
                        is BusinessException -> {
                            // "이미 예약된 좌석입니다" 등의 메시지를 가진 예외
                            businessExceptionCount.getAndIncrement()
                        }
                        is PessimisticLockingFailureException -> {
                            // 락을 기다리다가 타임아웃 발생 (QueryHints timeout 설정 관련)
                            lockTimeoutCount.getAndIncrement()
                        }
                        else -> {
                            // 예상치 못한 에러 확인용 로그
                            println("Unknown Error: ${e.javaClass.simpleName} - ${e.message}")
                        }
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        // Then
        val finalSeat = seatRepository.findById(targetSeatId).orElseThrow()
        val totalReservations = reservationRepository.count()

        println("=== 비관적 락 동시성 테스트 결과 ===")
        println("총 소요 시간: ${totalTime}ms")
        println("총 시도: $numberOfThreads")
        println("성공: ${successCount.get()}")
        println("실패: ${failCount.get()}")
        println("  ㄴ 이미 예약됨(BusinessException): ${businessExceptionCount.get()}")
        println("  ㄴ 락 타임아웃(Timeout): ${lockTimeoutCount.get()}")
        println("최종 좌석 상태: ${finalSeat.status}")
        println("DB 예약 데이터 수: $totalReservations")
        println("================================")

        // 1. 성공은 오직 1건
        assertEquals(1, successCount.get())

        // 2. 실패는 99건
        assertEquals(numberOfThreads - 1, failCount.get())

        // 3. 대부분의 실패 원인은 BusinessException이어야 함 (순차적으로 처리되면서 거절당함)
        // (환경에 따라 타임아웃이 발생할 수도 있어서 failCount 전체로 비교하는 것이 안전)
        assertEquals(numberOfThreads - 1, businessExceptionCount.get() + lockTimeoutCount.get())

        // 4. 데이터 정합성 확인
        // 좌석 상태가 예약 중(TEMPORARY 등)이어야 함
        assertEquals(SeatStatus.TEMPORARY, finalSeat.status)
        // 예약 테이블에는 데이터가 1개만 있어야 함
        assertEquals(1, totalReservations)
    }
}