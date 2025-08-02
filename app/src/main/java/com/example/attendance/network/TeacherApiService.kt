package com.example.attendance.network

import com.example.attendance.data.models.AttendanceResponse
import com.example.attendance.data.models.RollCallCreateResponse
import com.example.attendance.data.models.RollCallResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TeacherApiService {
    @POST("/api/teachers/roll_call/create")
    suspend fun createRollCall(
        @Query("timestamp") timestamp: String? = null
    ): Response<RollCallCreateResponse>

    @GET("/api/teachers/roll_call/{roll_call_id}")
    suspend fun getRollCall(
        @Path("roll_call_id") rollCallId: String
    ): Response<RollCallResponse>

    @GET("/api/teachers/roll_call/{roll_call_id}/attendance")
    suspend fun getAttendanceByRollCall(
        @Path("roll_call_id") rollCallId: String
    ): Response<List<AttendanceResponse>>

}