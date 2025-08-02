package com.example.attendance.data

import com.example.attendance.data.models.AttendanceRequest
import com.example.attendance.data.models.CreateAttendanceResponse
import com.example.attendance.network.StudentApiService

class StudentRepository(
    private val apiService: StudentApiService
) {
    suspend fun createAttendance(request: AttendanceRequest): Result<CreateAttendanceResponse> {
        return try {
            val response = apiService.createAttendance(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: CreateAttendanceResponse("No data"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}