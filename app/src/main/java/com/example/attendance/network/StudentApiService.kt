package com.example.attendance.network

import com.example.attendance.data.models.AttendanceRequest
import com.example.attendance.data.models.CreateAttendanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StudentApiService {
    @POST("/api/students/attendance/create")
    suspend fun createAttendance(
        @Body request: AttendanceRequest
    ): Response<CreateAttendanceResponse>
}