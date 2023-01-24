package com.example.pseudorandomgenerator

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class WifiNoiseActivity : AppCompatActivity() {
    private lateinit var btnWifiNoise: Button;
    private lateinit var progressBarWifiNoise: ProgressBar;
    private lateinit var txtWifiNoiseBarPercent: TextView;
    private lateinit var txtWifiNoise: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        initViews()
        progressBarWifiNoise.max = EnvVariables.DESIRED_LENGTH

        initListeners()

    }

    private fun initListeners() {
        btnWifiNoise.setOnClickListener {
            wifiNoiseData()
        }
    }

    private fun initViews() {
        btnWifiNoise = findViewById(R.id.btnWifiNoise)
        progressBarWifiNoise = findViewById(R.id.progressBarWifiNoise)
        txtWifiNoiseBarPercent = findViewById(R.id.txtWifiNoiseBarPercent)
        txtWifiNoise = findViewById(R.id.txtWifiNoise)
    }

    private fun wifiNoiseData() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "WiFi is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        wifiManager.startScan()

        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.checkAndRequestPermissions(this)
        }

        val stringBuilder = StringBuilder()

        for (result in wifiManager.scanResults) {
            val formattedResult = formatResults(result)
            stringBuilder.append(formattedResult)

            updateProgressInUi(stringBuilder)
        }

        if (!enoughData(stringBuilder)) {
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
        progressBarWifiNoise.progress = stringBuilder.toString().length

        val percentage =
            (progressBarWifiNoise.progress.toFloat() / progressBarWifiNoise.max.toFloat()) * 100
        txtWifiNoiseBarPercent.text = "${percentage.toInt()}%"
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