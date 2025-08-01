package com.example.attendance.data.models

data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String
)
