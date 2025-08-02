package com.example.attendance.data.models

import com.google.gson.annotations.SerializedName

data class AttendanceRequest(
    @SerializedName("roll_call_id")
    val rollCallId: String,
    @SerializedName("student_username")
    val username: String,
)


data class CreateAttendanceResponse(
    val message: String
)
