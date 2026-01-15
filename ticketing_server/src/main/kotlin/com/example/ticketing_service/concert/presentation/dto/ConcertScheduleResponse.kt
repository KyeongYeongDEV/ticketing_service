package com.example.ticketing_service.concert.presentation.dto

import com.example.ticketing_service.concert.domain.ConcertSchedule
import java.time.LocalDateTime

data class ConcertScheduleResponse(
    val id : Long,
    val concertDate : LocalDateTime,
    val totalSeats : Int
){
    companion object{
        fun from(schedule: ConcertSchedule) : ConcertScheduleResponse {
            return ConcertScheduleResponse(
                id = schedule.id!!,
                concertDate =  schedule.concertDate,
                totalSeats = schedule.totalSeats
            )
        }
    }
}
