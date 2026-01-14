package com.example.ticketing_service.payment.domain

import java.time.LocalDateTime

interface PaymentRepository {
    fun save(payment : Payment) : Payment
    fun findAllByStatusAndCreatedAtBefore(status: PaymentStatus, timeLimit: LocalDateTime): List<Payment>
}