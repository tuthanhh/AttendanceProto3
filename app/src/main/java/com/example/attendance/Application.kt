package com.example.attendance

import android.app.Application
import com.example.attendance.data.AppContainer

class AttendanceApp : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}