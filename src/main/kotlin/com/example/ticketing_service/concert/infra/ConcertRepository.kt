package com.example.ticketing_service.concert.infra

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertJpaRepository : JpaRepository<Concert, Long>

interface ConcertScheduleJpaRepository : JpaRepository<ConcertSchedule, Long> {
    fun findAllByConcertID(concertId : Long) : List<ConcertSchedule>
}