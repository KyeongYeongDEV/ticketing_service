package com.example.ticketing_service.concert.domain

import java.util.Optional

interface ConcertRepository {
    fun save(concert: Concert): Concert
    fun findAll(): List<Concert>
    fun findById(id: Long): Optional<Concert>
}

interface ConcertScheduleRepository {
    fun save(schedule: ConcertSchedule): ConcertSchedule
    fun findAllByConcertId(concertId: Long): List<ConcertSchedule>
}