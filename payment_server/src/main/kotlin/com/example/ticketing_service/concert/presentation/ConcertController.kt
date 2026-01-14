package com.example.ticketing_service.concert.presentation

import com.example.ticketing_service.concert.application.ConcertService
import com.example.ticketing_service.concert.presentation.dto.ConcertResponse
import com.example.ticketing_service.concert.presentation.dto.ConcertScheduleResponse
import com.example.ticketing_service.global.common.response.ApiResponse
import com.example.ticketing_service.seat.application.SeatService
import com.example.ticketing_service.seat.presentation.dto.SeatResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/concerts")
class ConcertController(
    private val concertService: ConcertService,
    private val seatService: SeatService
) {
    @GetMapping
    fun getConcerts(): ResponseEntity<ApiResponse<List<ConcertResponse>>> {
        val concerts = concertService.getConcerts()
        return ResponseEntity.ok(ApiResponse.success(concerts))
    }

    @GetMapping("/{concertId}/schedules")
    fun getSchedules(@PathVariable concertId: Long): ResponseEntity<ApiResponse<List<ConcertScheduleResponse>>> {
        val schedules = concertService.getSchedules(concertId)
        return ResponseEntity.ok(ApiResponse.success(schedules))
    }

    @GetMapping("/schedules/{scheduleId}/seats")
    fun getSeats(@PathVariable scheduleId: Long): ResponseEntity<ApiResponse<List<SeatResponse>>> {
        val seats = seatService.getAvailableSeats(scheduleId)
        return ResponseEntity.ok(ApiResponse.success(seats))
    }
}