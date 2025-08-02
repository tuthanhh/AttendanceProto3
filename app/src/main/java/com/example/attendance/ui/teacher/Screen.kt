package com.example.attendance.ui.teacher

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance.ui.MainActivity
import com.example.attendance.R
import com.example.attendance.ui.student.StudentViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN])
@Composable
fun TeacherHomeScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val status by viewModel.status

    val bluetoothAdvertisePermission = Manifest.permission.BLUETOOTH_ADVERTISE

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val advertiseGranted = permissions[bluetoothAdvertisePermission] ?: false

        if (advertiseGranted) {
            viewModel.startRollCall()
        } else {
            Toast.makeText(context, "Advertise permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    val attendanceResult = viewModel.attendanceList.collectAsState().value

    val rollCallId = viewModel.currentRollCallId.value
    val isBeaconOn = viewModel.isBeaconOn.value
    LaunchedEffect(rollCallId, isBeaconOn) {
        if (rollCallId.isNotBlank() && isBeaconOn) {
            while (true) {
                viewModel.fetchAttendanceList()
                delay(2000L)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Teacher Home", style = MaterialTheme.typography.headlineSmall)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Status: $status", style = MaterialTheme.typography.bodyLarge)
                if (viewModel.currentRollCallId.value != "") {
                    Text(
                        viewModel.currentRollCallId.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text("No active roll call", style = MaterialTheme.typography.bodyMedium)
                }

                when {
                    attendanceResult == null -> {
                        Text("Attendance not loaded yet.")
                    }
                    attendanceResult.isSuccess -> {
                        val list = attendanceResult.getOrNull() ?: emptyList()
                        Text("Attendance: ${list.size} students")
                    }
                    attendanceResult.isFailure -> {
                        val error = attendanceResult.exceptionOrNull()
                        if (error == null) {
                            Text("Error: Unknown error")
                        } else if (error.message?.contains("404") == true) {
                            Text("No attendance found for this roll call.")
                        } else {
                            Text("Error: ${error?.message ?: "Unknown error"}")
                        }
                    }
                }

                Button(onClick = {
                    permissionLauncher.launch(
                        arrayOf(bluetoothAdvertisePermission)
                    )
                }) {
                    Text("Start Beacon")
                }


                Button(onClick = {
                    viewModel.stopBeacon()
                }) {
                    Text("Stop Beacon")
                }
            }
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN])
@Preview(showBackground = true)
@Composable
 private fun TeacherHomeScreenPreview() {
    val fakeViewModel = viewModel<TeacherViewModel>(
        factory = TeacherViewModel.Factory
    )
    TeacherHomeScreen(
        viewModel = fakeViewModel,
        modifier = Modifier.fillMaxSize()
    )
    
}