package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.reservation.domain.ReservationStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationCancelService(
    private val reservationRepository: ReservationRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cancelReservation(reservationId: Long) {
        log.info("[Queue Event] 예약({}) 만료 감지, 상태 확인 시작", reservationId)

        val reservation = reservationRepository.findById(reservationId).orElse(null)

        if (reservation == null) {
            log.warn("[CancelService] 예약을 찾을 수 없음 (ID: {})", reservationId)
            return
        }

        if (reservation.status == ReservationStatus.PENDING) {
            reservation.cancel()
            reservation.seat.cancel()

            log.info("[CancelService] 예약({}) 취소 및 좌석({}) 반환 완료", reservationId, reservation.seat.id)
        } else {
            log.warn("[CancelService] 취소 스킵 - 현재 상태: {}", reservation.status)
        }
    }
}