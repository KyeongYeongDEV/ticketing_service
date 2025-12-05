package com.example.ticketing_service.reservation.presentation.dto

import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import jakarta.validation.constraints.Positive

data class ReservationRequestDto(
    @field:Positive(message = "사용자 ID는 양수여야 합니다")
    val userId : Long,

    @field:Positive(message = "좌석 ID는 양수여야 합니다")
    val seatId : Long
) {
    fun toCommand() : ReserveSeatCommand {
        return ReserveSeatCommand(
            userId = this.userId,
            seatId = this.seatId
        )
    }
}
