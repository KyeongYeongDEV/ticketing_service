package com.example.ticketing_service.concert.infra

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertRepository : JpaRepository<Concert, Long>

interface ConcertScheduleRepository : JpaRepository<ConcertSchedule, Long> {
    fun findAllByConcertID(concertId : Long) : List<ConcertSchedule>
}