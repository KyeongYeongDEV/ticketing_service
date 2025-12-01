package com.example.ticketing_service.seat.application

import com.example.ticketing_service.seat.domain.SeatStatus
import com.example.ticketing_service.seat.infra.SeatRepository
import com.example.ticketing_service.seat.presentation.dto.SeatResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeatService(
    private val seatRepository: SeatRepository
) {
    @Transactional(readOnly = true)
    fun getAvailableSeats(scheduleId: Long): List<SeatResponse> {
        // 예약 가능한 좌석만 조회
        return seatRepository.findAllByScheduleIdAndStatus(scheduleId, SeatStatus.AVAILABLE)
            .map { SeatResponse.from(it) }
    }
}