package com.example.ticketing_service.reservation.infra

import com.example.ticketing_service.reservation.domain.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ReservationJpaRepository : JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.seat WHERE r.id = :id")
    fun findByIdOptimized(@Param("id") id: Long): Optional<Reservation>
}