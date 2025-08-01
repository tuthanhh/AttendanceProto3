package com.example.attendance.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.attendance.AttendanceApp
import com.example.attendance.data.AppContainer
import com.example.attendance.ui.theme.AttendanceProto3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as AttendanceApp).container
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grants ->
            val ok = grants.entries.all { it.value }
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        ))
        setContent {
            App(
                appContainer
            )
        }
    }
}

@Composable
fun App(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    AttendanceProto3Theme {
        Scaffold { innerPadding ->

            AppNavGraph(
                appContainer,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}
