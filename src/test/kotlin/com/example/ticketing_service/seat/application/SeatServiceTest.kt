package com.example.ticketing_service.seat.application

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatStatus
import com.example.ticketing_service.seat.infra.SeatRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class SeatServiceTest {
    @MockK
    lateinit var seatRepository: SeatRepository

    @InjectMockKs
    lateinit var seatService : SeatService

    @Test
    @DisplayName("특정 스케줄의 예약 가능한 좌석을 조회한다.")
    fun get_available_seats_success() {
        val scheduleId = 1L
        val dummySchedule = ConcertSchedule(
            concert = Concert(title = "C", description = "D"),
            concertDate = LocalDateTime.now(),
            totalSeats = 50
        )
        val seats = listOf(
            Seat.create(dummySchedule, 1, BigDecimal("10000")),
            Seat.create(dummySchedule, 2, BigDecimal("10000"))
        )

        // 리플렉션으로 ID 주입
        seats.forEachIndexed { index, seat ->
            val idField = Seat::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(seat, (index + 1).toLong())
        }

        every{
            seatRepository.findAllByScheduleIdAndStatus(scheduleId, SeatStatus.AVAILABLE)
        } returns seats

        val result = seatService.getAvailableSeats(scheduleId)

        assertEquals(2, result.size)
        assertEquals(1, result[0].seatNo)
        assertEquals("AVAILABLE", result[0].status)
    }
}