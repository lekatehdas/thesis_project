package com.example.data_generator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.utilities.PermissionHelper

class WifiInformationDataGenerator(private val context: Context) {
    private var wifiData = ByteArray(0)
    private val seen = mutableSetOf<String>()

    fun getWifiInformationData(): ByteArray {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(context, "WiFi is not enabled", Toast.LENGTH_SHORT).show()
            return ByteArray(0)
        }

        val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionHelper = PermissionHelper(context as Activity)
            permissionHelper.checkAndAskPermissions()
        }

        wifiManager.startScan()

        for (result in wifiManager.scanResults) {
            val data = getResults(result)

            if (data != null) wifiData += data
        }
        println(wifiData.size)

        return wifiData
    }

    private fun getResults(result: ScanResult): ByteArray? {
        if (result.BSSID.substring(0, 9) in seen)
            return null

        seen.add(result.BSSID.substring(0, 9))

        val timestamp = result.timestamp.toString()
        return (result.BSSID.replace(":", "") +
                result.level.toString().replace("-", "") +
                result.frequency +
                timestamp.substring(timestamp.length - 4) +
                result.centerFreq0).toByteArray()
    }
}