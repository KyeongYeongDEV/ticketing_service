package com.example.ticketing_service.user.infra.persistance

import com.example.ticketing_service.user.domain.User

interface UserRepository {
    fun save(user : User) : User
    fun findById(id : Long) : User?
}