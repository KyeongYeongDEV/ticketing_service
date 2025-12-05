package com.example.ticketing_service.seat.infra

import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatRepository
import com.example.ticketing_service.seat.domain.SeatStatus
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class SeatRepositoryImpl(
    private val seatJpaRepository: SeatJpaRepository
) : SeatRepository {

    override fun save(seat: Seat): Seat = seatJpaRepository.save(seat)
    override fun saveAll(seats: List<Seat>): List<Seat> = seatJpaRepository.saveAll(seats)
    override fun findById(id: Long): Optional<Seat> = seatJpaRepository.findById(id)

    override fun findAllByScheduleIdAndStatus(scheduleId: Long, status: SeatStatus): List<Seat> {
        return seatJpaRepository.findAllByScheduleIdAndStatus(scheduleId, status)
    }
}