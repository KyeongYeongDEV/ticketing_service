package com.example.ticketing_service.seat.domain

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.user.domain.User
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals

class SeatTest {
    private val dummySchule = ConcertSchedule(
        concert = Concert(title = "테스트 콘서트", description = "설명"),
        concertDate = LocalDateTime.now(),
        totalSeats = 50
    )

    @Test
    @DisplayName("정상적인 좌석 생성 성공")
    fun create_success() {
        val seat = Seat.create(dummySchule, 1, BigDecimal("10000"))
        assertEquals(1, seat.seatNo)
        assertEquals(SeatStatus.AVAILABLE, seat.status)
    }

    @Test
    @DisplayName("가격이 0이하면 예외 발생")
    fun create_fail_invalid_price() {
        val ex = assertThrows(BusinessException::class.java){
            Seat.create(dummySchule, 1, BigDecimal("-100"))
        }
        assertEquals(ErrorCode.INVALID_INPUT_VALUE, ex.errorCode)
    }

    @Test
    @DisplayName("AVAILABLE 상태인 좌석은 점유 가능")
    fun hold_seat_success(){
        val seat = Seat.create(dummySchule, 1, BigDecimal("10000"))

        seat.hold() // 좌석 예약 -> 결제 진행중

        assertEquals(SeatStatus.TEMPORARY, seat.status)
    }

    @Test
    @DisplayName("이미 예약중인 좌석을 점유하려면 예외 발생")
    fun hold_seat_fail_already_reserved() {
        val seat = Seat.create(dummySchule, 1, BigDecimal("10000"))

        seat.hold() // test 위한 강제 상태 변경
        seat.confirm()

        val ex = assertThrows(BusinessException::class.java) {
            seat.hold()
        }
        assertEquals(ErrorCode.SEAT_ALREADY_RESERVED, ex.errorCode)

    }
}