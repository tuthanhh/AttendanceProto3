package com.example.attendance.data

import android.content.Context
import com.example.attendance.network.StudentApiService
import com.example.attendance.network.TeacherApiService
import com.example.attendance.network.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class AppContainer() {

    private val baseUrl = "https://attendanceproto3-server.onrender.com"
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .baseUrl(baseUrl)
        .build()
    val userRepository: UserRepository by lazy {
        UserRepository(
            retrofit.create(UserApiService::class.java)
        )
    }
    val studentRepository: StudentRepository by lazy {
        StudentRepository(
            retrofit.create(StudentApiService::class.java)
        )
    }
    val teacherRepository: TeacherRepository by lazy {
        TeacherRepository(
            retrofit.create(TeacherApiService::class.java)
        )
    }
}