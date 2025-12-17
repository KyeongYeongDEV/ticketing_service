package com.example.ticketing_service.payment.presentation

import com.example.ticketing_service.global.common.response.ApiResponse
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.payment.application.PaymentService
import com.example.ticketing_service.payment.presentation.dto.PaymentFailRequest
import com.example.ticketing_service.payment.presentation.dto.PaymentRequest
import com.example.ticketing_service.payment.presentation.dto.PaymentSuccessRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody request: PaymentRequest): ResponseEntity<ApiResponse<Long>> {
        val paymentId = paymentService.processPayment(request.toCommand())

        return ResponseEntity.ok(
            ApiResponse.success(paymentId, "결제가 성공적으로 완료되었습니다.")
        )
    }

    @GetMapping("/success")
    fun successPayment(@ModelAttribute request: PaymentSuccessRequest): ResponseEntity<ApiResponse<Long>> {

        val paymentId = paymentService.processPayment(request.toCommand())

        return ResponseEntity.ok(
            ApiResponse.success(paymentId, "결제가 정상적으로 처리되었습니다.")
        )
    }

    @GetMapping("/fail")
    fun failPayment(@ModelAttribute request: PaymentFailRequest): ResponseEntity<ApiResponse<Unit>> {
        log.error("Toss Payment Failed - $request")

        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ErrorCode.PAYMENT_FAILED))
    }
}