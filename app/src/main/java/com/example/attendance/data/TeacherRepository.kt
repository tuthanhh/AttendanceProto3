package com.example.attendance.data

import com.example.attendance.data.models.AttendanceResponse
import com.example.attendance.data.models.RollCallCreateResponse
import com.example.attendance.data.models.RollCallResponse
import com.example.attendance.network.TeacherApiService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TeacherRepository (
    private val apiService: TeacherApiService
) {

    suspend fun createRollCall(): Result<RollCallCreateResponse> {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        return try {
            val response = apiService.createRollCall(timestamp)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRollCall(rollCallId: String): Result<RollCallResponse> {
        return try {
            val response = apiService.getRollCall(rollCallId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAttendanceByRollCall(rollCallId: String): Result<List<AttendanceResponse>> {
        return try {
            val response = apiService.getAttendanceByRollCall(rollCallId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}