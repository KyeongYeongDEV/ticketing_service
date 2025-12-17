package com.example.ticketing_service.reservation.applicaion

import com.example.ticketing_service.reservation.application.ReservationService
import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.seat.domain.SeatRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired private lateinit var reservationService : ReservationService
    @Autowired private lateinit var seatRepository: SeatRepository
    @Autowired private lateinit var reservationRepository: ReservationRepository

    private val targetSeatId = 1L
    private val userId = 1L

    @BeforeEach
    fun setUp() {
        reservationRepository.de
    }
}