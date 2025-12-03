package com.example.ticketing_service.reservation.presentation.dto

import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand

data class ReservationRequestDto(
    val userId : Long,
    val seatId : Long
) {
    fun toCommand() : ReserveSeatCommand {
        return ReserveSeatCommand(
            userId = this.userId,
            seatId = this.seatId
        )
    }
}
