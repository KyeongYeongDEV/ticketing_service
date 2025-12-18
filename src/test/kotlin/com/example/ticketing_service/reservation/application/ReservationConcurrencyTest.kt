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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest // 통합 테스트 (실제 DB 및 빈 사용)
class ReservationConcurrencyTest {

    @Autowired private lateinit var reservationService: ReservationService
    @Autowired private lateinit var seatRepository: SeatRepository
    @Autowired private lateinit var reservationRepository: ReservationRepository
    @Autowired private lateinit var testDatabaseInitializer: TestDatabaseInitializer

    private val targetSeatId = 1L
    private val userId = 1L

    @BeforeEach
    fun setUp() {
        testDatabaseInitializer.clearAndInitialize()
    }

    @Test
    @DisplayName("좌석 1개를 100명이 동시에 예약 시도 -> 낙관적 락과 DB 제약조건으로 방어")
    fun concurrencyTest() {
        // Given
        val numberOfThreads = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(numberOfThreads)

        // 결과 확인용 카운터들
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)
        val optimisticLockConflictCount = AtomicInteger(0) // 낙관적 락 충돌 횟수
        val dbConstraintConflictCount = AtomicInteger(0)   // DB 유니크 제약 충돌 횟수

        // When
        for (i in 1..numberOfThreads) {
            executorService.submit {
                try {
                    val command = ReserveSeatCommand(userId = userId, seatId = targetSeatId)
                    reservationService.reserveSeat(command)

                    successCount.getAndIncrement()
                } catch (e: Exception) {
                    failCount.getAndIncrement()

                    when (e) {
                        is ObjectOptimisticLockingFailureException -> {
                            optimisticLockConflictCount.getAndIncrement()
                        }
                        is DataIntegrityViolationException -> {
                            dbConstraintConflictCount.getAndIncrement()
                        }
                        is BusinessException -> {
                            optimisticLockConflictCount.getAndIncrement()
                        }
                        else -> { // 실패 분류
                            if (e.cause is ObjectOptimisticLockingFailureException) {
                                optimisticLockConflictCount.getAndIncrement()
                            } else {
                                dbConstraintConflictCount.getAndIncrement()
                            }
                        }
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        // Then
        val finalSeat = seatRepository.findById(targetSeatId).orElseThrow()
        val totalReservations = reservationRepository.count()

        println("=== 동시성 테스트 상세 결과 ===")
        println("총 시도: $numberOfThreads")
        println("성공: ${successCount.get()}")
        println("실패: ${failCount.get()}")
        println("  ㄴ 낙관적 락(버전) 충돌: ${optimisticLockConflictCount.get()}")
        println("  ㄴ DB 유니크 제약 충돌: ${dbConstraintConflictCount.get()}")
        println("최종 좌석 상태: ${finalSeat.status}")
        println("DB 예약 데이터 수: $totalReservations")
        println("==========================")

        // 검증: 성공은 오직 1건
        assertEquals(1, successCount.get())
        assertEquals(numberOfThreads - 1, failCount.get())

        // 상태 변경 및 데이터 정합성 확인
        assertEquals(SeatStatus.TEMPORARY, finalSeat.status)
        assertEquals(1, totalReservations)
    }
}