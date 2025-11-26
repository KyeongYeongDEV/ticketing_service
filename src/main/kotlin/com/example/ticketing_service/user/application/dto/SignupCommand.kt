package com.example.ticketing_service.user.application.dto

data class SignupCommand(
    val name : String,
    val email : String,
    val password : String
)
