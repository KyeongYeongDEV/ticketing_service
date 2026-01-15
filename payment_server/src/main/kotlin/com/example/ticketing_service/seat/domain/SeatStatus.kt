package com.example.ticketing_service.seat.domain

enum class SeatStatus {
    AVAILABLE,  // 판매 가능
    TEMPORARY,  // 예약 중
    SOLD        // 판매 완료
}
