package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import com.example.ticketing_service.reservation.domain.Reservation
import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.reservation.domain.ReservationStatus
import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatStatus
import com.example.ticketing_service.seat.infra.SeatJpaRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class ReservationServiceTest {
    @MockK
    lateinit var reservationRepository : ReservationRepository

    @MockK
    lateinit var seatRepository : SeatJpaRepository

    // Seat 엔터티의 hold() 메소드를 spy로 감시하기 위함
    @MockK(relaxUnitFun = true)
    lateinit var mockSeat : Seat

    @InjectMockKs
    lateinit var  reservationService : ReservationService

    private val dummySchedule = ConcertSchedule(
        id = null,
        concert = Concert(title = "C", description = "D"),
        concertDate = LocalDateTime.now(),
        totalSeats = 50
    )

    @Test
    @DisplayName("정상적인 좌석 예약 시 임시 점유 상태(TEMPORARY)로 성공한다")
    fun reserve_seat_success() {
        val command = ReserveSeatCommand(userId = 1L, seatId = 1L)

        val availableSeat = Seat.create(dummySchedule, 1, BigDecimal("10000"))

        every { seatRepository.findById(1L) } returns Optional.of(availableSeat)

        val reservationSlot = slot<Reservation>()

        every { reservationRepository.save(capture(reservationSlot)) } answers {
            val inputReservation = reservationSlot.captured

            val idField = Reservation::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(inputReservation, 2L)

            inputReservation
        }

        val response = reservationService.reserveSeat(command)

        assertEquals(2L, response.reservationId)
        assertEquals(SeatStatus.TEMPORARY, availableSeat.status)
        assertEquals(ReservationStatus.PENDING, reservationSlot.captured.status)
    }

    @Test
    @DisplayName("요청된 좌석 ID가 존재하지 않으면 SEAT_NOT_FOUND 예외가 발생한다")
    fun reserve_seat_fail_seat_not_found() {
        val command = ReserveSeatCommand(userId = 1L, seatId = 999L)

        every { seatRepository.findById(999L) } returns Optional.empty()

        val ex = assertThrows(BusinessException::class.java) {
            reservationService.reserveSeat(command)
        }
        assertEquals(ErrorCode.SEAT_NOT_FOUND, ex.errorCode)
    }

    @Test
    @DisplayName("좌석이 이미 예약된 상태면 SEAT_ALREADY_RESERVED 예외가 발생한다")
    fun reserve_seat_fail_already_held() {
        val command = ReserveSeatCommand(userId = 1L, seatId = 1L)

        val heldSeat = Seat.create(dummySchedule, 1, BigDecimal("10000")).apply { status = SeatStatus.TEMPORARY }
        every { seatRepository.findById(1L) } returns Optional.of(heldSeat)

        // seat.hold() 메서드 내부에서 SEAT_ALREADY_RESERVED가 터지는지 확인
        val ex = assertThrows(BusinessException::class.java) {
            reservationService.reserveSeat(command)
        }
        assertEquals(ErrorCode.SEAT_ALREADY_RESERVED, ex.errorCode)
    }
}