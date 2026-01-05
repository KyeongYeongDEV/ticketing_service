package com.example.ticketing_service.payment.domain

import java.math.BigDecimal

interface PaymentClient {
    fun confirm(paymentKey : String, orderId : String, amount : BigDecimal) : String
}