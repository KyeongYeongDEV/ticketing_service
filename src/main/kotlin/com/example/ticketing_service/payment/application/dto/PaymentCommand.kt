package com.example.ticketing_service.payment.application.dto

import java.math.BigDecimal

data class PaymentCommand (
    val reservationId : Long,
    val paymentKey : String,
    val amount : BigDecimal
)