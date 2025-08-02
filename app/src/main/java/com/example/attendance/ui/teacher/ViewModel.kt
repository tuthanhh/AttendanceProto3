package com.example.attendance.ui.teacher

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
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.attendance.AttendanceApp
import com.example.attendance.data.TeacherRepository
import com.example.attendance.data.models.AttendanceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S) // Android 12+
class TeacherViewModel(
    private val teacherRepository: TeacherRepository,
) : ViewModel() {


    var status = mutableStateOf("Idle")
        private set
    var currentRollCallId = mutableStateOf<String>("")
        private set

    var isBeaconOn = mutableStateOf(false)
        private set

    private val serviceUuid = ParcelUuid.fromString("0000ABCD-0000-1000-8000-00805F9B34FB")

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val advertiser: BluetoothLeAdvertiser? by lazy {
        bluetoothAdapter?.bluetoothLeAdvertiser
    }
    private val advertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .setConnectable(false)
        .build()

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d("BLE", "Advertising started")
            status.value = "Beacon ON"
        }

        override fun onStartFailure(errorCode: Int) {

            super.onStartFailure(errorCode)
            val message = when (errorCode) {
                AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE -> "Data too large"
                AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Too many advertisers"
                AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED -> "Already started"
                AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR -> "Internal error"
                AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "Feature unsupported"
                else -> "Unknown error"
            }
            Log.e("BLE", "Advertise failed: $errorCode ($message)")
            status.value = "Advertise failed: $message"
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun startBeacon(
        advertiseData: AdvertiseData
    ) {
        isBeaconOn.value = true
        advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
            ?: run { status.value = "No BLE advertiser" }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopBeacon() {
        advertiser?.stopAdvertising(advertiseCallback)
        status.value = "Beacon OFF"
        currentRollCallId.value = ""

        _attendanceList.value = null
        isBeaconOn.value = false
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_ADVERTISE)
    fun startRollCall() {

        viewModelScope.launch {
            val result = teacherRepository.createRollCall()

            result.onSuccess { rollCallId ->
                status.value = "Roll call created with id: ${rollCallId.rollCallId}"
                currentRollCallId.value = rollCallId.rollCallId

                val uuid = UUID.fromString(rollCallId.rollCallId)
                val uuidBytes = ByteBuffer.allocate(16)
                    .putLong(uuid.mostSignificantBits)
                    .putLong(uuid.leastSignificantBits)
                    .array()

                val advertiseData = AdvertiseData.Builder()
                    .setIncludeDeviceName(false) // saves space
                    .addServiceUuid(serviceUuid)
                    .addServiceData(serviceUuid, uuidBytes) // ✅ 16 bytes only
                    .build()
                startBeacon(advertiseData)
                // Optionally start beacon here
            }.onFailure { e ->
                status.value = "Failed to create roll call: ${e.message}"
            }
        }
    }



    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN])
    override fun onCleared() {
        super.onCleared()
        stopBeacon()
    }

    private var _attendanceList = MutableStateFlow<Result<List<AttendanceResponse>>?>(null)
    val attendanceList: StateFlow<Result<List<AttendanceResponse>>?> = _attendanceList

    fun fetchAttendanceList() {
        viewModelScope.launch {
            _attendanceList.value = teacherRepository.getAttendanceByRollCall(currentRollCallId.value)
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as AttendanceApp
                TeacherViewModel(
                    application.container.teacherRepository
                )
            }
        }
    }
}
