package com.example.ticketing_service.reservation.domain

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findById(id: Long): Optional<Reservation>
    fun findByIdWithSeat(id : Long) : Optional<Reservation>
}