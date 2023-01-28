package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.TrafficStats
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pseudorandomgenerator.databinding.ActivityNetworkTrafficBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import kotlinx.coroutines.*

class NetworkTrafficActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkTrafficBinding

    private var previousMobileBytesSent: Long = TrafficStats.getMobileTxBytes()
    private var previousMobileBytesReceived: Long = TrafficStats.getMobileRxBytes()
    private var previousMobilePacketsSent: Long = TrafficStats.getMobileTxPackets()
    private var previousMobilePacketsReceived: Long = TrafficStats.getMobileRxPackets()
    private var previousWifiBytesSent: Long =
        TrafficStats.getTotalTxBytes() - previousMobileBytesSent
    private var previousWifiBytesReceived: Long =
        TrafficStats.getTotalRxBytes() - previousMobileBytesReceived
    private var previousWifiPacketsSent: Long =
        TrafficStats.getTotalTxPackets() - previousMobilePacketsSent
    private var previousWifiPacketsReceived: Long =
        TrafficStats.getTotalRxPackets() - previousMobilePacketsReceived

    private var generatedData = ""
    private var isDataGenerating = false
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkTrafficBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarTraffic.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnTrafficStart.setOnClickListener {
            if (isDataGenerating) {
                isDataGenerating = false
                job?.cancel()
                runOnUiThread { updateProgressInUi() }
            } else {
                generateDataWithNetworkTraffic()
                isDataGenerating = true
            }
        }
    }

    private fun generateDataWithNetworkTraffic() {
        if (!hasInternetConnection()) {
            Toast.makeText(this, "No internet available", Toast.LENGTH_SHORT).show()
            return
        }

        job = CoroutineScope(Job()).launch {
            while (isActive && generatedData.length < EnvVariables.DESIRED_LENGTH) {

                try {
                    generatedData += networkTrafficVolume()
                    runOnUiThread { updateProgressInUi() }
                    delay(1500)

                } catch (ex: Exception) {
                    Log.d(TAG, ex.message.toString())
                }
            }

            if (enoughData()) {
                saveData()
                runOnUiThread { resetUiElements() }
                resetData()
            }
        }

    }

    private fun enoughData(): Boolean {
        return generatedData.length >= EnvVariables.DESIRED_LENGTH
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun networkTrafficVolume(): String {
        val stringBuilder = StringBuilder()
        val mobileBytesSent = TrafficStats.getMobileTxBytes()
        val mobileBytesReceived = TrafficStats.getMobileRxBytes()
        val mobilePacketsSent = TrafficStats.getMobileTxPackets()
        val mobilePacketsReceived = TrafficStats.getMobileRxPackets()
        val wifiBytesSent = TrafficStats.getTotalTxBytes() - mobileBytesSent
        val wifiBytesReceived = TrafficStats.getTotalRxBytes() - mobileBytesReceived
        val wifiPacketsSent = TrafficStats.getTotalTxPackets() - mobilePacketsSent
        val wifiPacketsReceived = TrafficStats.getTotalRxPackets() - mobilePacketsReceived

        if ((mobileBytesSent - previousMobileBytesSent) != 0L || (mobileBytesReceived - previousMobileBytesReceived) != 0L) {
            stringBuilder.append("${mobileBytesSent - previousMobileBytesSent}${mobileBytesReceived - previousMobileBytesReceived}")
        }
        if ((mobilePacketsSent - previousMobilePacketsSent) != 0L || (mobilePacketsReceived - previousMobilePacketsReceived) != 0L) {
            stringBuilder.append("${mobilePacketsSent - previousMobilePacketsSent}${mobilePacketsReceived - previousMobilePacketsReceived}")
        }
        if ((wifiBytesSent - previousWifiBytesSent) != 0L || (wifiBytesReceived - previousWifiBytesReceived) != 0L) {
            stringBuilder.append("${wifiBytesSent - previousWifiBytesSent}${wifiBytesReceived - previousWifiBytesReceived}")
        }
        if ((wifiPacketsSent - previousWifiPacketsSent) != 0L || (wifiPacketsReceived - previousWifiPacketsReceived) != 0L) {
            stringBuilder.append("${wifiPacketsSent - previousWifiPacketsSent}${wifiPacketsReceived - previousWifiPacketsReceived}")
        }

        previousMobileBytesSent = mobileBytesSent
        previousMobileBytesReceived = mobileBytesReceived
        previousMobilePacketsSent = mobilePacketsSent
        previousMobilePacketsReceived = mobilePacketsReceived
        previousWifiBytesSent = wifiBytesSent
        previousWifiBytesReceived = wifiBytesReceived
        previousWifiPacketsSent = wifiPacketsSent
        previousWifiPacketsReceived = wifiPacketsReceived

        return stringBuilder.toString()
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "traffic"
        )
    }

    private fun resetData() {
        generatedData = ""
        isDataGenerating = false
    }

    @SuppressLint("SetTextI18n")
    private fun resetUiElements() {
        binding.progressBarTraffic.progress = 0
        binding.txtTrafficPercent.text = "0%"
        binding.btnTrafficStart.text = "START"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi() {
        binding.progressBarTraffic.progress = generatedData.length
        binding.btnTrafficStart.text = if (isDataGenerating) "PAUSE" else "START"

        val percentage =
            (binding.progressBarTraffic.progress.toFloat() / binding.progressBarTraffic.max.toFloat()) * 100
        binding.txtTrafficPercent.text = "${percentage.toInt()}%"
    }
}