package com.example.ticketing_service.payment.presentation.dto

import com.example.ticketing_service.payment.application.dto.PaymentCommand
import java.math.BigDecimal

data class PaymentRequest(
    val reservationId: Long,
    val paymentKey: String,
    val amount: BigDecimal
) {
    fun toCommand() = PaymentCommand(reservationId, paymentKey, amount)
}