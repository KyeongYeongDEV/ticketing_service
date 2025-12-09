package com.example.ticketing_service.payment.domain

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.reservation.domain.Reservation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payments")
class Payment private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    val reservation: Reservation,

    @Column(nullable = false)
    val paymentKey : String,

    @Column(nullable = false)
    val amount : BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status : PaymentStatus
) : BaseEntity() {
    companion object {
        fun create(reservation : Reservation, paymentKey : String, amount : BigDecimal) : Payment {
            return Payment(
                reservation = reservation,
                paymentKey = paymentKey,
                amount = amount,
                status = PaymentStatus.DONE
            )
        }
    }
}


//private constructor를 사용하는 이유?
// 정적 팩토리 메소드를 사용하는 이유
// @GeneratedValue(strategy = GenerationType.IDENTITY) 전부 다 설명
//@OneToOne(fetch = FetchType.LAZY)
//@JoinColumn(name = "reservation_id", nullable = false)
//BaseEntity()를 사용하는 이유