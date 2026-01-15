package com.example.ticketing_service.user.domain

interface UserRepository {
    fun save(user : User) : User
    fun findById(id : Long) : User?
}