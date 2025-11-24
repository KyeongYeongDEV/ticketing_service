package com.example.ticketing_service.seat.domain

import com.example.ticketing_service.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

enum class SeatStatus {
    AVAILABLE,  // 판매 가능
    TEMPORARY,  // 예약 중
    SOLD        // 판매 완료
}

@Entity
@Table(
    name = "seat"
)
class Seat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id") // ERD의 PK 이름 반영
    val id: Long? = null,

    //TODO: ManyToOne 달 것
    @Column(name = "concert_schedule_id", nullable = false)
    val scheduleId: Long,

    @Column(name = "seat_no", nullable = false)
    val seatNo: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: SeatStatus = SeatStatus.AVAILABLE,

    //  낙관적 락 test를 위함
    @Version
    var version: Long = 0

) : BaseEntity() {

    // 예약중 (AVAILABLE -> TEMPORARY)
    fun hold() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw IllegalStateException("이미 선택된 좌석입니다.")
        }
        this.status = SeatStatus.TEMPORARY
    }

    // 예약 확정 (TEMPORARY -> SOLD)
    fun confirm() {
        if (this.status != SeatStatus.TEMPORARY) {
            throw IllegalStateException("예약된 좌석이 아닙니다.")
        }
        this.status = SeatStatus.SOLD
    }

    // 예약 취소 OR 만료 (TEMPORARY -> AVAILABLE)
    fun cancel() {
        this.status = SeatStatus.AVAILABLE
    }
}