package com.example.attendance.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.attendance.data.AppContainer
import com.example.attendance.data.models.AuthState
import com.example.attendance.ui.login.LoginScreen
import com.example.attendance.ui.login.LoginViewModel
import com.example.attendance.ui.student.StudentHomeScreen
import com.example.attendance.ui.student.StudentViewModel
import com.example.attendance.ui.teacher.TeacherHomeScreen
import com.example.attendance.ui.teacher.TeacherViewModel

@Composable
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_SCAN])
fun AppNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {

    val authState by appContainer.userRepository.authState.collectAsState()

    when (authState) {
        AuthState.Loading -> {
            // Show loading state, e.g., a progress indicator
        }
        else -> NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = when (authState) {
                is AuthState.Authenticated -> "home/${(authState as AuthState.Authenticated).role}"
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
            composable (
                route = "home/{role}",
                arguments = listOf(
                    navArgument("role") {
                        defaultValue = "Student"
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ){ backStackEntry ->
                val role = backStackEntry.arguments?.getString("role") ?: "user"
                Log.d("AppNavGraph2", "Navigating to home with role: $role")
                when (role) {
                    "Teacher" -> {
                        val viewModel = viewModel<TeacherViewModel>(
                            factory = TeacherViewModel.Factory
                        )
                        TeacherHomeScreen(
                            viewModel = viewModel,
                            modifier = modifier
                        )
                    }
                    "Student" -> {
                        val viewModel = viewModel<StudentViewModel>(
                            factory = StudentViewModel.Factory
                        )
                        StudentHomeScreen(
                            viewModel = viewModel,
                            modifier = modifier
                        )
                    }
                    else -> {
                        // Handle unknown role or default case
                    }
                }
            }
        }
    }
}
