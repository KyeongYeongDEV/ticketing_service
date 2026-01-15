package com.example.ticketing_service.user.domain

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    @DisplayName("정상적인 정보로 유저 생성 시 성공한다")
    fun create_success() {
        val user = User.create("신짱구", "test@email.com", "password")
        assertEquals("test@email.com", user.email)
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 INVALID_EMAIL_FORMAT 예외가 발생한다")
    fun create_fail_invalid_email() {
        val invalidEmail = "invalidEmail"

        val exception = assertThrows(BusinessException::class.java) {
            User.create("신짱구", invalidEmail, "password")
        }

        assertEquals(ErrorCode.INVALID_EMAIL_FORMAT, exception.errorCode)
    }
}