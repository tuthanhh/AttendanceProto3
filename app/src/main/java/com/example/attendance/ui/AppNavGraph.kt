package com.example.attendance.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendance.data.AppContainer
import com.example.attendance.data.models.AuthState
import com.example.attendance.ui.login.LoginScreen
import com.example.attendance.ui.login.LoginViewModel

@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val authState = produceState<AuthState>(initialValue = AuthState.Loading) {
        value = appContainer.userRepository.authState.value
    }

    when (authState.value) {
        AuthState.Loading -> {
            // Show loading state, e.g., a progress indicator
        }
        else -> NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = when (authState.value) {
                is AuthState.Authenticated -> "home"
                else -> "login"
            },
        ) {
            composable(route = "login") {
                val viewModel = viewModel<LoginViewModel>(
                    factory = LoginViewModel.Factory
                )
                LoginScreen(
                    viewModel,
                    onSuccess = { role ->
                        navController.navigate("home/$role")
                    },

                )
            }
            composable (route = "home/{role}") { backStackEntry ->
                val role = backStackEntry.arguments?.getString("role") ?: "user"
                when (role) {
                    "Teacher" -> {
                        // Navigate to Teacher's home screen
                    }
                    "Student" -> {
                        // Navigate to Student's home screen
                    }
                    else -> {
                        // Handle unknown role or default case
                    }
                }
            }
        }
    }
}
