package com.example.ticketing_service.user.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.user.application.dto.SignupCommand
import com.example.ticketing_service.user.domain.User
import com.example.ticketing_service.user.domain.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    @DisplayName("회원가입 성공 시 ID를 반환한다")
    fun signup_success() {
        val command = SignupCommand("신짱구", "new@email.com", "pw")
        val user = User.create("신짱구", "new@email.com", "pw")

        val userId = User::class.java.getDeclaredField("id")
        userId.isAccessible = true
        userId.set(user, 1L) // DB 대신 userId 생성

        every { userRepository.save(any()) } returns user

        val resultId = userService.signup(command)

        assertEquals(1L, resultId)
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면(DB 제약조건 위반) 비즈니스 예외를 던진다")
    fun signup_fail_duplicate() {
        val command = SignupCommand("신짱구", "exist@email.com", "pw")

        every { userRepository.save(any()) } throws DataIntegrityViolationException("Duplicate entry")

        val ex = assertThrows(BusinessException::class.java) {
            userService.signup(command)
        }
        assertEquals(ErrorCode.EMAIL_DUPLICATION, ex.errorCode)
    }
}