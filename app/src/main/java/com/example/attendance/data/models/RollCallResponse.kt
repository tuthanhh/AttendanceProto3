package com.example.attendance.data.models

import com.google.gson.annotations.SerializedName

data class RollCallCreateResponse(
    val message:String,
    @SerializedName("roll_call_id")
    val rollCallId: String,
)

data class RollCallResponse(
    val id: String,
    @SerializedName("created_at")
    val createdAt: String // or LocalDateTime with custom adapter
)