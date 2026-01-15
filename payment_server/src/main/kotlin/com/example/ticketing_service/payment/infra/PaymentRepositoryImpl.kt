package com.example.ticketing_service.payment.infra

import com.example.ticketing_service.payment.domain.Payment
import com.example.ticketing_service.payment.domain.PaymentRepository
import com.example.ticketing_service.payment.domain.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface PaymentJpaRepository : JpaRepository<Payment, Long> {
    fun findAllByStatusAndCreatedAtBefore(status: PaymentStatus, timeLimit: LocalDateTime): List<Payment>
}

@Repository
class PaymentRepositoryImpl (
    private val jpaRepository: PaymentJpaRepository
) : PaymentRepository {
    override  fun save(payment : Payment) : Payment {
        return jpaRepository.save(payment)
    }

    override fun findAllByStatusAndCreatedAtBefore(status: PaymentStatus, timeLimit: LocalDateTime): List<Payment> {
        return jpaRepository.findAllByStatusAndCreatedAtBefore(status, timeLimit)
    }
}