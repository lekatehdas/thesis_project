package com.example.utilities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class PermissionHelper(private val activity: Activity) {
    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
    )

    fun checkAndAskPermissions() {
        val ungrantedPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toTypedArray(), 0)
        }
    }
}
