package com.example.ticketing_service.payment.domain

interface PaymentRepository {
    fun save(payment : Payment) : Payment
}