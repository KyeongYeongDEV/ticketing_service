package com.example.ticketing_service.seat.presentation.dto

import com.example.ticketing_service.seat.domain.Seat
import java.math.BigDecimal

data class SeatResponse(
    val id : Long,
    val seatNo : Int,
    val price : BigDecimal,
    val status : String
){
    companion object {
        fun from(seat : Seat) : SeatResponse {
            return SeatResponse(
                id = seat.id!!,
                seatNo = seat.seatNo,
                price = seat.price,
                status = seat.status.name
            )
        }
    }
}
