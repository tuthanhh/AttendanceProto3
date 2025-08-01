package com.example.attendance.data

import com.example.attendance.data.models.AuthState
import com.example.attendance.data.models.LoginRequest
import com.example.attendance.data.models.RegisterRequest
import com.example.attendance.network.UserApiService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException

class UserRepository (
    private val apiService: UserApiService,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun register(username: String, password: String, role: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequest(username, password, role))
                if (response.isSuccessful) {
                    _authState.value = AuthState.Authenticated(role)
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.code()}")
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error: ${e.localizedMessage}")
            } catch (e: HttpException) {
                _authState.value = AuthState.Error("HTTP error: ${e.code()}")
            }
        }
    }


    suspend fun login(username: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _authState.value = AuthState.Authenticated(body.role)
                    } else {
                        _authState.value = AuthState.Error("Empty response")
                    }
                } else {
                    _authState.value = AuthState.Error("Login failed: ${response.code()}")
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error: ${e.localizedMessage}")
            } catch (e: HttpException) {
                _authState.value = AuthState.Error("HTTP error: ${e.code()}")
            }
        }
    }
}