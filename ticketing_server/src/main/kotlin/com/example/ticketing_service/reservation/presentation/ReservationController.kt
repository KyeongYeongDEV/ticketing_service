package com.example.ticketing_service.reservation.presentation

import com.example.ticketing_service.global.common.response.ApiResponse
import com.example.ticketing_service.reservation.application.ReservationFacade // [수정] Facade 임포트
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.presentation.dto.ReservationRequestDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/reservations")
class ReservationController (
    private val reservationFacade : ReservationFacade
){
    @PostMapping
    fun createReservation(
        @RequestBody @Valid request : ReservationRequestDto
    ) : ResponseEntity<ApiResponse<ReservationResponse>> {
        val command = request.toCommand()

        val reservationResponse = reservationFacade.reserveSeat(command)

        val uri = URI.create("/api/reservations/${reservationResponse.reservationId}")

        return ResponseEntity
            .created(uri)
            .body(ApiResponse.success(
                reservationResponse,
                "좌석이 임시 선점되었습니다. 5분 내에 결제해 주세요"
            ))
    }
}