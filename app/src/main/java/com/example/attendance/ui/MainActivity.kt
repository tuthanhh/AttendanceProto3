package com.example.attendance.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
