package com.example.pseudorandomgenerator

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.pseudorandomgenerator.databinding.ActivityWifiBinding

class WifiInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWifiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarWifiInfo.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    private fun initListeners() {
        binding.btnWifiInfo.setOnClickListener {
            wifiNoiseData()
        }
    }

    private fun wifiNoiseData() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "WiFi is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionHelper = PermissionHelper(this@WifiInfoActivity)
            permissionHelper.checkAndAskPermissions()
        }

        wifiManager.startScan()

        val generatedData = StringBuilder()

        for (result in wifiManager.scanResults) {
            val formattedResult = formatResults(result)
            generatedData.append(formattedResult)

            updateProgressInUi(generatedData)
        }

        if (!enoughData(generatedData)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("""
                Not enough data to generate key.
                Refresh the wifi search and try again.
                If problem persists, try in the area with multiple wifi connection points.
            """.trimIndent())

            builder.create().show()
        } else {
//            TODO("Trim the results and save to database")
        }
    }

    private fun enoughData(stringBuilder: StringBuilder) =
        stringBuilder.toString().length >= EnvVariables.DESIRED_LENGTH

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi(stringBuilder: StringBuilder) {
        binding.progressBarWifiInfo.progress = stringBuilder.toString().length

        val percentage =
            (binding.progressBarWifiInfo.progress.toFloat() / binding.progressBarWifiInfo.max.toFloat()) * 100
        binding.txtWifiInfoBarPercent.text = "${percentage.toInt()}%"
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