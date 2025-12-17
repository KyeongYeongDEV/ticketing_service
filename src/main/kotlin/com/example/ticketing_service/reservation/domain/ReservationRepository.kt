package com.example.ticketing_service.reservation.domain

import java.util.Optional

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findById(id: Long): Optional<Reservation>
    fun findByIdWithSeat(id : Long) : Optional<Reservation>

    fun count(): Long
    fun deleteAll()
}