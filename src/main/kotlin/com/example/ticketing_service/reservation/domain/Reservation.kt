package com.example.ticketing_service.reservation.domain

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.seat.domain.Seat
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
import java.time.LocalDateTime

@Entity
@Table(name = "reservations")
class Reservation private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId : Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    val seat : Seat,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus,

    @Column(nullable = false)
    val expiredAt: LocalDateTime // 결제 만료 시간 (TTL)
    ) : BaseEntity() {
        protected constructor() : this(
            userId = 0,
            seat = Seat.createJpaDummy(),
            status = ReservationStatus.PENDING,
            expiredAt = LocalDateTime.MIN
        )

        companion object{
            fun create(userId : Long, seat : Seat) : Reservation {
                // 만료 시간 5분
                val expiredAt = LocalDateTime.now().plusMinutes(5)

                return Reservation(
                    userId = userId,
                    seat = seat,
                    status = ReservationStatus.PENDING,
                    expiredAt = expiredAt
                )
            }
        }

    fun confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }
        this.status = ReservationStatus.CONFIRMED
    }

    fun cancel() {
        this.status = ReservationStatus.CANCELLED
    }
}