package com.example.attendance.network

import com.example.attendance.data.models.LoginRequest
import com.example.attendance.data.models.LoginResponse
import com.example.attendance.data.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface UserApiService {
    @POST("/api/users/register")
    suspend fun register (
        @Body request: RegisterRequest
    ) : Response<Unit>

    @POST("/api/users/login")
    suspend fun login (
        @Body request: LoginRequest
    ): Response<LoginResponse>
}