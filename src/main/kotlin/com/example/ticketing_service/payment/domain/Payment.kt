package com.example.ticketing_service.payment.domain

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.reservation.domain.Reservation
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "payments")
class Payment private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    val reservation: Reservation,

    @Column(nullable = false)
    val paymentKey: String,

    @Column(nullable = false)
    val amount: BigDecimal
) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus = PaymentStatus.DONE
        protected set

    protected constructor() : this(
        reservation = Reservation.createDummy(),
        paymentKey = "",
        amount = BigDecimal.ZERO
    )

    companion object {
        fun create(reservation: Reservation, paymentKey: String, amount: BigDecimal): Payment {
            return Payment(
                reservation = reservation,
                paymentKey = paymentKey,
                amount = amount
            )
        }
    }
}