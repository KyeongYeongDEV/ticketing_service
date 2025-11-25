package com.example.ticketing_service.user.presentation.dto

data class SignupRequest(
    val name : String,
    val email : String,
    val password : String
)
