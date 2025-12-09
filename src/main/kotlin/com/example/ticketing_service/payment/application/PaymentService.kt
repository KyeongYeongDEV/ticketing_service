package com.example.ticketing_service.payment.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.payment.application.dto.PaymentCommand
import com.example.ticketing_service.payment.domain.Payment
import com.example.ticketing_service.payment.domain.PaymentClient
import com.example.ticketing_service.payment.domain.PaymentRepository
import com.example.ticketing_service.reservation.domain.ReservationRepository
import jakarta.transaction.Transactional
import java.time.LocalDateTime

class PaymentService (
    private val paymentRepository : PaymentRepository,
    private val reservationRepository : ReservationRepository,
    private val paymentClient : PaymentClient
){
    @Transactional
    fun processPayment(command : PaymentCommand) : Long {
        val reservation = reservationRepository.findByIdWithSeat(command.reservationId)
            .orElseThrow { BusinessException(ErrorCode.RESERVATION_NOT_FOUND) }

        if (reservation.expiredAt.isBefore(LocalDateTime.now())){
            throw BusinessException(ErrorCode.RESERVATION_EXPIRED)
        }

        if (reservation.seat.price.compareTo(command.amount) != 0 ) {
            throw BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT)
        }

        reservation.confirm()
        reservation.seat.confirm()

        val payment = Payment.create(reservation, command.paymentKey, command.amount)
        return paymentRepository.save(payment).id!!
    }
}