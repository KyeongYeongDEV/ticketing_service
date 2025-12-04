package com.example.ticketing_service.seat.infra

import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatStatus
import org.springframework.data.jpa.repository.JpaRepository

interface SeatJpaRepository : JpaRepository<Seat, Long> {
    fun findAllByScheduleIdAndStatus(scheduleId: Long, status: SeatStatus): List<Seat>
}