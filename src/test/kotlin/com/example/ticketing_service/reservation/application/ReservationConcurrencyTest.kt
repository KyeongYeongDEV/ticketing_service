package com.example.ticketing_service.reservation.application

// [중요] ReserveSeatCommand가 있는 패키지를 import 해야 합니다.
// (이미지에 나온 패키지 경로를 기반으로 작성했습니다. 만약 빨간줄이 뜨면 Alt+Enter로 import 하세요)
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
    @DisplayName("좌석 1개를 100명이 동시에 예약 시도")
    fun concurrencyTest() {
        // Given
        val numberOfThreads = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(numberOfThreads)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        // When
        for (i in 1..numberOfThreads) {
            executorService.submit {
                try {
                    // [수정 1] Command 객체로 감싸서 전달합니다.
                    // 기존: reservationService.reserveSeat(userId, targetSeatId) -> 에러 발생
                    val command = ReserveSeatCommand(userId = userId, seatId = targetSeatId)
                    reservationService.reserveSeat(command)

                    successCount.getAndIncrement()
                } catch (e: Exception) {
                    failCount.getAndIncrement()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        // Then
        val finalSeat = seatRepository.findById(targetSeatId).orElseThrow()

        // [수정 2] count 뒤에 괄호()를 붙여줍니다.
        val totalReservations = reservationRepository.count()

        println("=== 테스트 결과 ===")
        println("성공 횟수: ${successCount.get()}")
        println("실패 횟수: ${failCount.get()}")
        println("최종 좌석 상태: ${finalSeat.status}")
        println("DB에 생성된 총 예약 수: $totalReservations")
        println("=================")

        assertEquals(1, successCount.get())
        assertEquals(numberOfThreads - 1, failCount.get())
        assertEquals(SeatStatus.TEMPORARY, finalSeat.status)
        assertEquals(1, totalReservations)
    }
}