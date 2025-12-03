package com.example.ticketing_service.reservation.domain

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findById(id: Long): Optional<Reservation>
    fun findByIdWithSeat(id : Long) : Optional<Reservation>

    @Query("SELECT r FROM Reservation r JOIN FETCH r.seat WHERE r.id = :id")
    fun findByIdOptimized(@Param("id") id: Long): Optional<Reservation>
// TODO: 쿼리 성능이 저하되면 SELECT r FROM Reservation r 대신 SELECT NEW DTO(...)로 변경 고려
}