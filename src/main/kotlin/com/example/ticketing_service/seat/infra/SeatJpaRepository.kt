package com.example.ticketing_service.seat.infra

import com.example.ticketing_service.seat.domain.Seat
import com.example.ticketing_service.seat.domain.SeatStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface SeatJpaRepository : JpaRepository<Seat, Long> {
    fun findAllByScheduleIdAndStatus(scheduleId: Long, status: SeatStatus): List<Seat>

    // 비관적 락(쓰기 락)을 걸고 조회하는 쿼리
    // 이 쿼리가 실행되면 해당 Row는 트랜잭션이 끝날 때까지 다른 곳에서 수정/조회(락 종류에 따라 다름) 불가능
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :seatId")
    fun findByIdWithPessimisticLock(seatId: Long): Seat?
}