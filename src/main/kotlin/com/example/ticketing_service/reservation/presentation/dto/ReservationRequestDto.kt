package com.example.ticketing_service.reservation.presentation.dto

data class ReservationRequestDto(
    val userId : Long,
    val seatId : Long
)
