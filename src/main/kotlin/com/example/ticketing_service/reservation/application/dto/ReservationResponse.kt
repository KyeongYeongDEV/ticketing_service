package com.example.ticketing_service.reservation.application.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ReservationResponse(
    val reservationId : Long,
    val expiredAt : LocalDate,
    val seatNo : Long,
    val amount : BigDecimal
)
