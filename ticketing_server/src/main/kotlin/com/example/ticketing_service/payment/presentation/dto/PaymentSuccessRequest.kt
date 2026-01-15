package com.example.ticketing_service.payment.presentation.dto

import com.example.ticketing_service.payment.application.dto.PaymentCommand
import java.math.BigDecimal

data class PaymentSuccessRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: BigDecimal
) {
    fun toCommand(): PaymentCommand {
        return PaymentCommand(
            reservationId = orderId.toLong(),
            paymentKey = paymentKey,
            amount = amount
        )
    }
}