package com.example.ticketing_service.reservation.application.dto

data class ReserveSeatCommand(
    val userId: Long,
    val seatId: Long
)