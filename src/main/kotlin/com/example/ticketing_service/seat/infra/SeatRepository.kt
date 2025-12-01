package com.example.ticketing_service.seat.infra

import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatStatus
import org.springframework.data.jpa.repository.JpaRepository

interface SeatRepository : JpaRepository<Seat, Long> {
    // 예약 가능한 좌석만 조회
    fun findAllByScheduleIdAndStatus(schduleId : Long, status : SeatStatus) : List<Seat>
}