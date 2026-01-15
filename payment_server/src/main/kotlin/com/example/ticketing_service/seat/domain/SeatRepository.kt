package com.example.ticketing_service.seat.domain

import java.util.Optional

interface SeatRepository {
    fun save(seat: Seat): Seat
    fun saveAll(seats: List<Seat>): List<Seat>
    fun findById(id: Long): Optional<Seat>
    fun findAllByScheduleIdAndStatus(scheduleId: Long, status: SeatStatus): List<Seat>
    fun findByIdWithPessimisticLock(seatId: Long) : Seat?
}