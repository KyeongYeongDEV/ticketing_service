package com.example.ticketing_service.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

import com.example.ticketing_service.common.entity.BaseEntity
import com.example.ticketing_service.global.exception.BusinessException
import com.example.ticketing_service.global.exception.ErrorCode

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val name: String

) : BaseEntity() {
    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        fun create(name : String, email : String, password : String) : User {
            if(!EMAIL_PATTERN.matches(email)) {
                throw BusinessException(ErrorCode.INVALID_EMAIL_FORMAT)
            }

            return User(
                name = name,
                email = email,
                password = password
            )
        }
    }
}