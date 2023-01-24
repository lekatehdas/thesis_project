package com.example.pseudorandomgenerator

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//class PermissionHelper {
//
//    companion object {
//        private val MY_PERMISSIONS_REQUEST = 1
//
//        fun checkAndRequestPermissions(context: Context) {
//            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE)
//                != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(context as Activity,
//                    arrayOf(android.Manifest.permission.ACCESS_WIFI_STATE,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                        android.Manifest.permission.CAMERA),
//                    MY_PERMISSIONS_REQUEST)
//            }
//        }
//    }
//}

class PermissionHelper(private val activity: Activity) {
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
