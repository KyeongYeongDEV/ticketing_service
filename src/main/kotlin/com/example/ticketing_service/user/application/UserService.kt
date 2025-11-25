package com.example.ticketing_service.user.application

import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode
import com.example.ticketing_service.user.application.dto.SignupCommand
import com.example.ticketing_service.user.application.dto.UserResponse
import com.example.ticketing_service.user.domain.User
import com.example.ticketing_service.user.domain.UserRepository

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.convert.Jsr310Converters

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository
){
    @Transactional
fun signup(command : SignupCommand) : Long{
        // TODO: 비번 암호화
        val newUser = User(
            name = command.name,
            email = command.email,
            password = command.password
        )

        try {
            val savedUser = userRepository.save(newUser)

            return savedUser.id!!
        } catch (e : DataIntegrityViolationException){
            throw BusinessException(ErrorCode.EMAIL_DUPLICATION)
        }
    }


    @Transactional(readOnly = true)
    fun getUser(userId : Long) : UserResponse {
      val user = userRepository.findById(userId)
          ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        return UserResponse.from(user);
    }
}