package com.example.ticketing_service.user.application

import com.example.ticketing_service.user.domain.User
import com.example.ticketing_service.user.domain.UserRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository
){
    @Transactional
    fun signup(name : String, email : String, password : String) : Long{
        val newUser = User(name = name, email = email, password = password)
        return userRepository.save(newUser).id!!
    }

    @Transactional(readOnly = true)
    fun getUser(userId : Long) : User {
      return userRepository.findById(userId)
          ?: throw IllegalArgumentException("유저가 없습니다")
    }
}