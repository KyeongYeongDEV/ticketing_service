package com.example.ticketing_service.payment.presentation.dto

data class PaymentFailRequest(
    val code: String,
    val message: String,
    val orderId: String?
)