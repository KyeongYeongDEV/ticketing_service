package com.example.ticketing_service.seat.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatRepository
import com.example.ticketing_service.seat.domain.SeatStatus
import com.example.ticketing_service.seat.presentation.dto.SeatResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeatService(
    private val seatRepository: SeatRepository
) {
    @Transactional(readOnly = true)
    fun getAvailableSeats(scheduleId: Long): List<SeatResponse> {
        return seatRepository.findAllByScheduleIdAndStatus(scheduleId, SeatStatus.AVAILABLE)
            .map { SeatResponse.from(it) }
    }

    @Transactional
    fun getSeatForReservation(seatId: Long): Seat {
        return seatRepository.findByIdWithPessimisticLock(seatId)
            ?: throw BusinessException(ErrorCode.SEAT_NOT_FOUND)
    }
}