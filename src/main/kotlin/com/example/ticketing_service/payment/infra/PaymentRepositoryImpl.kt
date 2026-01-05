package com.example.ticketing_service.payment.infra

import com.example.ticketing_service.payment.domain.Payment
import com.example.ticketing_service.payment.domain.PaymentRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface PaymentJpaRepository : JpaRepository<Payment, Long>

@Repository
class PaymentRepositoryImpl (
    private val jpaRepository: PaymentJpaRepository
) : PaymentRepository {
    override  fun save(payment : Payment) : Payment {
        return jpaRepository.save(payment)
    }
}