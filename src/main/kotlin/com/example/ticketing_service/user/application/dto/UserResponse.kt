package com.example.ticketing_service.user.application.dto

import com.example.ticketing_service.user.domain.User

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String
) {
    companion object {
        fun from(user : User) : UserResponse {
            return UserResponse(
                id = user.id!!,
                name = user.name,
                email = user.email
            )
        }
    }
}
