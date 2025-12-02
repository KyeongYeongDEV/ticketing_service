package com.example.ticketing_service.concert.presentation

import com.example.ticketing_service.concert.application.ConcertService
import com.example.ticketing_service.concert.presentation.dto.ConcertResponse
import com.example.ticketing_service.seat.application.SeatService
import com.example.ticketing_service.seat.presentation.dto.SeatResponse
import com.ninjasquad.springmockk.MockkBean

import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext

@WebMvcTest(ConcertController::class)
class ConcertControllerTest {
    @Autowired
    lateinit var  mockMvc : MockMvc

    @MockkBean
    lateinit var  concertService : ConcertService

    @MockkBean
    lateinit var seatService : SeatService

    @MockkBean(relaxed = true)
    lateinit var jpaMetamodelMappingContext: JpaMetamodelMappingContext

    @Test
    @DisplayName("공연 목록 조회 호출시 200OK와 리스트를 반환한다.")
    fun get_concerts_success() {
        val concerts = listOf(
            ConcertResponse(1L, "아이유 콘서트", "김해")
        )
        every {
            concertService.getConcerts()
        } returns concerts

        mockMvc.perform(get("/api/concerts"))
            .andExpect (status().isOk)
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].title").value("아이유 콘서트"))
    }

    @Test
    @DisplayName("좌석 조회 호출 시 200 OK와 리스트를 반환한다.")
    fun get_seats_success() {
        val scheduleId = 1L
        val seats = listOf(
            SeatResponse(1L, 10, BigDecimal("15000"), "AVAILABLE")
        )
        every { seatService.getAvailableSeats(scheduleId) } returns seats

        mockMvc.perform(get("/api/concerts/schedules/$scheduleId/seats"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].seatNo").value(10))
            .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
    }


}