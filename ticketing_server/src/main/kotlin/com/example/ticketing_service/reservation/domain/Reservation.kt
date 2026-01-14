package com.example.ticketing_service.reservation.domain

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.seat.domain.Seat
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reservations")
class Reservation private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    val seat: Seat,

    @Column(nullable = false)
    val expiredAt: LocalDateTime

) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus = ReservationStatus.PENDING
        protected set

    protected constructor() : this(
        userId = 0,
        seat = Seat.createJpaDummy(),
        expiredAt = LocalDateTime.MIN
    )

    companion object {
        fun create(userId: Long, seat: Seat): Reservation {
            return Reservation(
                userId = userId,
                seat = seat,
                expiredAt = LocalDateTime.now().plusMinutes(5)
            )
        }

        fun createDummy(): Reservation {
            return Reservation()
        }
    }

    fun confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }
        this.status = ReservationStatus.CONFIRMED
    }

    fun cancel() {
        if (this.status == ReservationStatus.CONFIRMED) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }
        // 멱등성을 위해 이미 취소된 경우 에러를 던지지 않거나 필요 시 던짐
        if (this.status == ReservationStatus.CANCELLED) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }
        this.status = ReservationStatus.CANCELLED
    }
}