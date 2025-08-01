package com.example.attendance.ui.student

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.attendance.ui.MainActivity
import com.example.attendance.R
@RequiresApi(Build.VERSION_CODES.S)
@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN])
@Composable
fun StudentHomeScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val status by viewModel.status

    val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
    val bluetoothAdvertisePermission = Manifest.permission.BLUETOOTH_ADVERTISE

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val scanGranted = permissions[bluetoothScanPermission] ?: false
        val advertiseGranted = permissions[bluetoothAdvertisePermission] ?: false

        if (scanGranted) {
            viewModel.startScan()
        } else {
            Toast.makeText(context, "Scan permission denied", Toast.LENGTH_SHORT).show()
        }

        if (advertiseGranted) {
            viewModel.startBeacon()
        } else {
            Toast.makeText(context, "Advertise permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Student Home", style = MaterialTheme.typography.headlineSmall)
        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text("Status: $status", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = {
                permissionLauncher.launch(
                    arrayOf(bluetoothScanPermission)
                )
            }) {
                Text("Start Scanning")
            }

            Button(onClick = {
                viewModel.stopScan()
            }) {
                Text("Stop Scanning")
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
