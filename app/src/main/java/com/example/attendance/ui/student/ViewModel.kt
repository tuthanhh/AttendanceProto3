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
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.nio.charset.Charset
@RequiresApi(Build.VERSION_CODES.S) // Android 12+
class StudentViewModel : ViewModel() {

    var status = mutableStateOf("Idle")
        private set

    private val serviceUuid = ParcelUuid.fromString("0000ABCD-0000-1000-8000-00805F9B34FB")
    private val beaconData = "Lecture123".toByteArray(Charset.defaultCharset())

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val advertiser: BluetoothLeAdvertiser? by lazy {
        bluetoothAdapter?.bluetoothLeAdvertiser
    }

    private val scanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private val advertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .setConnectable(false)
        .build()

    private val advertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .addServiceUuid(serviceUuid)
        .addServiceData(serviceUuid, beaconData)
        .build()

    private val scanFilter = ScanFilter.Builder()
        .setServiceUuid(serviceUuid)
        .build()

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d("BLE", "Advertising started")
            status.value = "Beacon ON"
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.e("BLE", "Advertise failed: $errorCode")
            status.value = "Advertise failed: $errorCode"
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val data = result.scanRecord?.getServiceData(serviceUuid)
            if (data != null) {
                val txt = String(data, Charset.defaultCharset())
                Log.d("BLE", "Beacon detected: $txt")
                status.value = "Detected beacon: $txt"
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE", "Scan failed: $errorCode")
            status.value = "Scan failed: $errorCode"
        }
    }

    fun startBeacon() {
        advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
            ?: run { status.value = "No BLE advertiser" }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopBeacon() {
        advertiser?.stopAdvertising(advertiseCallback)
        status.value = "Beacon OFF"
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
        stopBeacon()
        stopScan()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudentViewModel()
            }
        }
    }
}
