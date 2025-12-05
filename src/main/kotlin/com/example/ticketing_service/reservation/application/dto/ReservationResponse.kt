package com.example.ticketing_service.reservation.application.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ReservationResponse(
    val reservationId : Long,
    val expiredAt : LocalDateTime,
    val seatNo : Int,
    val amount : BigDecimal
)
