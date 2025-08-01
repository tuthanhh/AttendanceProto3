package com.example.attendance.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance.data.models.AuthState
import com.example.attendance.ui.theme.AttendanceProto3Theme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel.authState) {
        if (viewModel.authState is AuthState.Authenticated) {
            onSuccess(viewModel.authState.role)
        }
    }

}

@Preview
@Composable
private fun LoginScreenPreview() {
    AttendanceProto3Theme {
        LoginScreen(
            viewModel = viewModel(),
            onSuccess = {}
        )
    }
}