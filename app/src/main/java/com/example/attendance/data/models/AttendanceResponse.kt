package com.example.attendance.data.models

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    val id: String,
    @SerializedName("roll_call_id")
    val rollCallId: String,
    @SerializedName("student_username")
    val username: String,
    val timestamp: String,
)
