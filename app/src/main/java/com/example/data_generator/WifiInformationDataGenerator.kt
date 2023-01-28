package com.example.data_generator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.utilities.PermissionHelper

class WifiInformationDataGenerator(private val context: Context) {
    private var generatedData = ""

    fun getWifiInformationData(): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(context, "WiFi is not enabled", Toast.LENGTH_SHORT).show()
            return ""
        }

        val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionHelper = PermissionHelper(context as Activity)
            permissionHelper.checkAndAskPermissions()
        }

        wifiManager.startScan()

        for (result in wifiManager.scanResults) {
            val formattedResult = formatResults(result)
            generatedData += formattedResult
        }

        return generatedData
    }

    private fun formatResults(result: ScanResult): Any {
        val timestamp = result.timestamp.toString()
        return result.BSSID +
                result.level +
                result.frequency +
                timestamp.substring(timestamp.length - 4) +
                result.centerFreq0
    }
}