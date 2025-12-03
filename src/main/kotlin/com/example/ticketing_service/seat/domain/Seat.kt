package com.example.ticketing_service.seat.domain

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.concert.domain.Concert
import com.example.ticketing_service.concert.domain.ConcertSchedule
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

enum class SeatStatus {
    AVAILABLE,  // 판매 가능
    TEMPORARY,  // 예약 중
    SOLD        // 판매 완료
}

@Entity
@Table(
    name = "seats",
    indexes = [
        Index(name = "idx_schedule_status", columnList = "schedule_id, status")
    ]
)
class Seat private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: ConcertSchedule,

    @Column(name = "seat_no", nullable = false)
    val seatNo: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SeatStatus = SeatStatus.AVAILABLE,

    @Version // 낙천적 락은 위함
    var version: Long = 0

) : BaseEntity() {

    // JPA를 위한 protected 생성자 (빈 객체 생성)
    protected constructor() : this(
        id = null,
        schedule = ConcertSchedule(
            concert = Concert(title = "", description = ""),
            concertDate = LocalDateTime.now(),
            totalSeats = 0
        ),
        seatNo = 0,
        price = BigDecimal.ZERO,
        status = SeatStatus.AVAILABLE,
        version = 0
    )

    // 좌석 점유
    fun hold() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw BusinessException(ErrorCode.SEAT_ALREADY_RESERVED)
        }
        this.status = SeatStatus.TEMPORARY
    }

    fun confirm() {
        if (this.status != SeatStatus.TEMPORARY) {
            // 점유 상태여야 결제 가능
            // 아니면 에러
            throw BusinessException(ErrorCode.SEAT_NOT_FOUND)
        }
        this.status = SeatStatus.SOLD
    }

    fun cancel() {
        // 만료 -> 점유 취소
        this.status = SeatStatus.AVAILABLE
    }

    companion object {
        fun create(schedule: ConcertSchedule, seatNo: Int, price: BigDecimal): Seat {
            if (price <= BigDecimal.ZERO) {
                throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
            }
            if (seatNo <= 0) {
                throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
            }

            return Seat(
                schedule = schedule,
                seatNo = seatNo,
                price = price
            )
        }

        private fun createDummySchedule(): ConcertSchedule {
            return ConcertSchedule(
                concert = Concert(title = "", description = ""),
                concertDate = LocalDateTime.now(),
                totalSeats = 0
            )
        }

        internal fun createJpaDummy(): Seat {
            return Seat(
                id = null,
                schedule = createDummySchedule(),
                seatNo = 0,
                price = BigDecimal.ZERO,
                status = SeatStatus.AVAILABLE,
                version = 0
            )
        }
    }
}