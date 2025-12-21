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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class ReservationConcurrencyTest {

    // [변경 1] Service 대신 Facade를 주입받습니다. (락 진입점)
    @Autowired private lateinit var reservationFacade: ReservationFacade

    @Autowired private lateinit var seatRepository: SeatRepository
    @Autowired private lateinit var reservationRepository: ReservationRepository
    @Autowired private lateinit var testDatabaseInitializer: TestDatabaseInitializer

    private val targetSeatId = 1L

    @BeforeEach
    fun setUp() {
        testDatabaseInitializer.clearAndInitialize()
    }

    @Test
    @DisplayName("Redis 분산 락 적용: 100명이 동시에 예약을 시도하면 1명만 성공하고 나머지는 실패해야 한다")
    fun redisDistributedLockTest() {
        val numberOfThreads = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(numberOfThreads)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val lockFailCount = AtomicInteger(0)      // 락 획득 실패
        val alreadyReservedCount = AtomicInteger(0) // 락은 뚫었으나 이미 예약된 경우

        val startTime = System.currentTimeMillis()

        for (i in 1..numberOfThreads) {
            executorService.submit {
                try {
                    val command = ReserveSeatCommand(userId = i.toLong(), seatId = targetSeatId)

                    // Facade 호출
                    reservationFacade.reserveSeat(command)

                    successCount.getAndIncrement()
                } catch (e: Exception) {
                    failCount.getAndIncrement()

                    if (e is BusinessException) {
                        println("실패 사유: ${e.errorCode} - ${e.message}")
                    } else {
                        println("Unknown Error: ${e.javaClass.simpleName} - ${e.message}")
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        val finalSeat = seatRepository.findById(targetSeatId).orElseThrow()
        val totalReservations = reservationRepository.count()

        println("=== Redis 분산 락 테스트 결과 ===")
        println("총 소요 시간: ${totalTime}ms")
        println("총 시도: $numberOfThreads")
        println("성공: ${successCount.get()}")
        println("실패: ${failCount.get()}")
        println("최종 좌석 상태: ${finalSeat.status}")
        println("DB 예약 데이터 수: $totalReservations")
        println("================================")

        assertEquals(1, successCount.get())
        assertEquals(numberOfThreads - 1, failCount.get())
        assertEquals(SeatStatus.TEMPORARY, finalSeat.status)
        assertEquals(1, totalReservations)
    }
}