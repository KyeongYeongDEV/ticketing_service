package com.example.ticketing_service.reservation.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.reservation.application.dto.ReservationResponse
import com.example.ticketing_service.reservation.application.dto.ReserveSeatCommand
import com.example.ticketing_service.reservation.domain.Reservation
import com.example.ticketing_service.reservation.domain.ReservationRepository
import com.example.ticketing_service.seat.domain.SeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository
) {
    /**
     * 비관적 락을 사용하여 좌석 예약
     * 1. 트랜잭션 시작
     * 2. select ... for update (락 획득 대기)
     * 3. 락 획득 후 데이터 상태 검증
     * 4. 예약 처리
     * 5. 트랜잭션 종료 (락 해제)
     */
    @Transactional
    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        // [수정] 일반 조회 대신 '비관적 락'이 걸린 조회를 사용
        val seat = seatRepository.findByIdWithPessimisticLock(command.seatId)


        // [중요] 락을 획득하고 들어왔더라도, 앞선 트랜잭션이 이미 예약을 끝냈을 수 있음.
        // 따라서 seat.hold() 내부에서 반드시 상태 체크(Available인지)를 해야 함.
        // 만약 seat.hold()에 검증 로직이 없다면 여기서 if(seat.status != AVAILABLE) 체크가 필요함.
        seat.hold()

        val reservation = Reservation.create(
            userId = command.userId,
            seat = seat
        )

        val savedReservation = reservationRepository.save(reservation)

        return ReservationResponse(
            reservationId = savedReservation.id!!,
            expiredAt = savedReservation.expiredAt,
            seatNo = seat.seatNo,
            amount = seat.price
        )
    }
}