package com.example.attendance.ui.student

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.attendance.AttendanceApp
import com.example.attendance.data.StudentRepository
import com.example.attendance.data.UserRepository
import com.example.attendance.data.models.AttendanceRequest
import com.example.attendance.data.models.AuthState
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S) // Android 12+
class StudentViewModel (
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    var status = mutableStateOf("Idle")
        private set

    // TODO: Replace with your actual service UUID and beacon data
    private val serviceUuid = ParcelUuid.fromString("0000ABCD-0000-1000-8000-00805F9B34FB")

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val scanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    private val scanFilter = ScanFilter.Builder()
        .setServiceUuid(serviceUuid)
        .build()

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {

        @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val data = result.scanRecord?.getServiceData(serviceUuid)
            if (data != null) {

                viewModelScope.launch {
                    val buffer = ByteBuffer.wrap(data)
                    val mostSigBits = buffer.long
                    val leastSigBits = buffer.long
                    val uuid = UUID(mostSigBits, leastSigBits)

                    Log.d("BLE", "Beacon detected: $uuid")
                    status.value = "Detected beacon: $uuid"

                    val response = studentRepository.createAttendance(
                        AttendanceRequest(
                            rollCallId = uuid.toString(),
                            username = (userRepository.authState.value as AuthState.Authenticated).username
                        )
                    )
                    response.onSuccess  {
                        status.value = "Attendance marked"
                        stopScan()
                    }.onFailure { e ->
                        if (e.message?.contains("400") == true) {
                            status.value = "Already marked attendance"
                        } else {
                            status.value = "Failed to mark attendance: ${e.message}"
                        }
                    }

                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE", "Scan failed: $errorCode")
            status.value = "Scan failed: $errorCode"
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        scanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
            ?: run { status.value = "No BLE scanner" }
        status.value = "Scanning..."
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        scanner?.stopScan(scanCallback)
        status.value = "Scan stopped"
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCleared() {
        super.onCleared()
        stopScan()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as AttendanceApp
                StudentViewModel(
                    application.container.studentRepository,
                    application.container.userRepository
                )
            }
        }
    }
}


