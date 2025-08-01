package com.example.attendance.ui.login

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance.data.models.AuthState
import com.example.attendance.ui.theme.AttendanceProto3Theme
import com.example.attendance.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            Log.d("Event", "Success login with role ${(authState as AuthState.Authenticated).role}")
            onSuccess((authState as AuthState.Authenticated).role)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_small)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.auth_page_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
            )
        }

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            label = {
                Text(
                    text = stringResource(R.string.auth_page_username_label),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            singleLine = true
        )
        PasswordOutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            modifier = Modifier .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            label = {
                Text(
                    text = stringResource(R.string.auth_page_password_label),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            singleLine = true
        )
        if (viewModel.authState is AuthState.Error) {
            Text(
                text = (viewModel.authState as AuthState.Error).msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
        // This field is only shown for registration, not login.
        if (viewModel.isLoggingState == false) {
            RoleDropdown(
                selectedRole = viewModel.role,
                onRoleChange = { viewModel.onRoleChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                Log.d("","Button clicked: isLoggingState = ${viewModel.isLoggingState}, username = ${viewModel.username}, password = ${viewModel.password}, role = ${viewModel.role}")
                if (viewModel.isLoggingState) {
                    Log.d("LoginScreen", "Logging in with username: ${viewModel.username} and password: ${viewModel.password}")
                    viewModel.login()
                } else {
                    viewModel.register()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(
                    if (viewModel.isLoggingState == true) R.string.auth_page_login_button_label
                    else R.string.auth_page_register_button_label
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }
        TextButton(
            onClick = {
                viewModel.changeLoggingState()
            },
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(
                    if (viewModel.isLoggingState == true) R.string.auth_page_register_button_label
                    else R.string.auth_page_login_button_label
                ),
                style = MaterialTheme.typography.labelMedium,

                )


        }
    }
}
@Composable
fun PasswordOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit),
    singleLine: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        trailingIcon = {
            IconButton(
                onClick = {
                    visible = !visible
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (visible) {
                            R.drawable.ic_visibility
                        } else {
                            R.drawable.ic_visibility_off
                        }
                    ),
                    contentDescription = null
                )
            }
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    selectedRole: String,
    onRoleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val roles = listOf("Student", "Teacher")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            readOnly = true,
            label = { Text("Role") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleChange(role)
                        expanded = false
                    }
                )
            }
        }
    }
}
