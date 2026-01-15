package com.example.ticketing_service.payment.scheduler

import com.example.ticketing_service.payment.domain.PaymentClient
import com.example.ticketing_service.payment.domain.PaymentRepository
import com.example.ticketing_service.payment.domain.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PaymentRecoveryScheduler(
    private val paymentRepository: PaymentRepository,
    private val paymentClient: PaymentClient
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 1000 * 60) // 1분마다 실행
    @Transactional
    fun recoverPendingPayments() {
        val timeLimit = LocalDateTime.now().minusMinutes(5)

        // 5분 넘게 PENDING인 건 조회
        val pendingPayments = paymentRepository.findAllByStatusAndCreatedAtBefore(PaymentStatus.PENDING, timeLimit)

        if (pendingPayments.isEmpty()) return

        log.info("검증 필요한 결제 건 발견: ${pendingPayments.size}건")

        pendingPayments.forEach { payment ->
            // 토스 API를 통해 실제 결제 상태 확인
            val actualStatus = paymentClient.validatePayment(payment.paymentKey)

            if (actualStatus == "DONE") {
                payment.complete()
                log.info("✅ 결제 복구 성공: ${payment.id}")
            } else {
                // 실패하거나 없는 결제는 취소 처리
                payment.cancel()
                log.info("❌ 결제 취소 처리: ${payment.id}")
            }
        }
    }
}