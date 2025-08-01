package com.example.attendance.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.attendance.AttendanceApp
import com.example.attendance.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val UserRepository: UserRepository,
) : ViewModel() {

    val authState = UserRepository.authState

    fun login(username: String, password: String) {
        // Logic to handle user login
        viewModelScope.launch {
            UserRepository.login(username, password)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as AttendanceApp
                LoginViewModel(
                    UserRepository = application.container.userRepository
                )
            }
        }
    }
}