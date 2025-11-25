package com.example.ticketing_service.user.presentation.dto

data class SignupCommand(
    val name : String,
    val email : String,
    val password : String
)
