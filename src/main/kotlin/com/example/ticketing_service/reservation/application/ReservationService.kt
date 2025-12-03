package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import com.example.ticketing_service.reservation.domain.Reservation
import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.seat.infra.SeatRepository
import org.springframework.transaction.annotation.Transactional

class ReservationService (
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository
) {
    @Transactional
    fun reserveSeat(command : ReserveSeatCommand) : ReservationResponse {
        val seat = seatRepository.findById(command.seatId)
            .orElseThrow { throw BusinessException(ErrorCode.SEAT_NOT_FOUND) }

        seat.hold()

        val reservation = Reservation.create(
            userId = command.userId,
            seat = seat
        )

        val savedReservation = reservationRepository.save(reservation)

        return ReservationResponse(
            reservationId = savedReservation.id!!,
            expiredAt = savedReservation.expiredAt,
            seatNo = seat.seatNo,
            amount = seat.price
        )
    }
}