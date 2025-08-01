package com.example.attendance.ui.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.attendance.AttendanceApp
import com.example.attendance.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    val authState = userRepository.authState

    var _isLoggingState = mutableStateOf(true)
    var _username = mutableStateOf("")
    var _password = mutableStateOf("")
    var _role = mutableStateOf("Student")

    val username: String
        get() = _username.value
    val password: String
        get() = _password.value
    val role: String
        get() = _role.value
    val isLoggingState: Boolean
        get() = _isLoggingState.value

    fun login() {
        // Logic to handle user login
        viewModelScope.launch {
            userRepository.login(_username.value, _password.value)
        }
    }

    fun register() {
        // Logic to handle user registration
        viewModelScope.launch {
            userRepository.register(_username.value, _password.value, _role.value)
        }
    }

    fun changeLoggingState() {
        _isLoggingState.value = !_isLoggingState.value
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onRoleChange(role: String) {
        _role.value = role
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as AttendanceApp
                LoginViewModel(
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}