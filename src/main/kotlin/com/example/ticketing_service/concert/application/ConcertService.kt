package com.example.ticketing_service.concert.application

import com.example.ticketing_service.concert.infra.ConcertRepository
import com.example.ticketing_service.concert.infra.ConcertScheduleRepository
import com.example.ticketing_service.concert.presentation.dto.ConcertResponse
import com.example.ticketing_service.concert.presentation.dto.ConcertScheduleResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConcertService (
    private val concertRepository : ConcertRepository,
    private val scheduleRepository : ConcertScheduleRepository
){
    @Transactional(readOnly = true)
    fun getConcert() : List<ConcertResponse> {
        return concertRepository.findAll()
            .map{ ConcertResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getSchedules(concertId : Long) : List<ConcertScheduleResponse> {
        return scheduleRepository.findAllByConcertID(concertId)
            .map { ConcertScheduleResponse.from(it) }
    }
}