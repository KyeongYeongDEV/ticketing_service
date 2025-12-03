package com.example.ticketing_service.reservation.infra

import com.example.ticketing_service.reservation.domain.Reservation
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationJpaRepository : JpaRepository<Reservation, Long>