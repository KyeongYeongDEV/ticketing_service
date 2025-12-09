package com.example.ticketing_service.payment.presentation

import com.example.ticketing_service.global.common.response.ApiResponse
import com.example.ticketing_service.payment.application.PaymentService
import com.example.ticketing_service.payment.presentation.dto.PaymentRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody request: PaymentRequest): ResponseEntity<ApiResponse<Long>> {
        val paymentId = paymentService.processPayment(request.toCommand())

        return ResponseEntity.ok(
            ApiResponse.success(paymentId, "결제가 성공적으로 완료되었습니다.")
        )
    }
}