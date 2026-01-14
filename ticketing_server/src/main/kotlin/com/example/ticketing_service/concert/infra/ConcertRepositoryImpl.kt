package com.example.ticketing_service.concert.infra

import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertRepository
import com.example.ticketing_service.concert.domain.ConcertSchedule
import com.example.ticketing_service.concert.domain.ConcertScheduleRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ConcertRepositoryImpl(
    private val concertJpaRepository: ConcertJpaRepository,
    private val scheduleJpaRepository: ConcertScheduleJpaRepository
) : ConcertRepository, ConcertScheduleRepository {

    // ConcertRepository 구현
    override fun save(concert: Concert): Concert = concertJpaRepository.save(concert)
    override fun findAll(): List<Concert> = concertJpaRepository.findAll()
    override fun findById(id: Long): Optional<Concert> = concertJpaRepository.findById(id)

    // ConcertScheduleRepository 구현
    override fun save(schedule: ConcertSchedule): ConcertSchedule = scheduleJpaRepository.save(schedule)
    override fun findAllByConcertId(concertId: Long): List<ConcertSchedule> = scheduleJpaRepository.findAllByConcertId(concertId)
}