package com.example.attendance.data

import android.content.Context
import com.example.attendance.network.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class AppContainer() {

    private val baseUrl = "http://10.0.2.2:8000"
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
}