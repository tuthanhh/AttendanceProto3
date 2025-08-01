package com.example.attendance.data.models

sealed class AuthState {
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    data class Authenticated(val role : String): AuthState()
    data class Error(val msg: String): AuthState()
}