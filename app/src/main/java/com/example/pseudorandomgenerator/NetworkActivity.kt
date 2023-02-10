package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_generator.NetworkTrafficDataGenerator
import com.example.pseudorandomgenerator.databinding.ActivityNetworkBinding
import com.example.utilities.ByteArrayListXOR
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import kotlinx.coroutines.*

class NetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkBinding

    private var networkData = ByteArray(0)

    private var isDataGenerating = false
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarNetwork.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnNetworkStart.setOnClickListener {
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

        val generator = NetworkTrafficDataGenerator(this)

        job = CoroutineScope(Job()).launch {
            while (true) {

                while (isActive && smallestArraySize() < EnvVariables.DESIRED_LENGTH) {

                    try {
                        networkData += generator.networkTrafficVolume()
                        runOnUiThread { updateProgressInUi() }
                        delay(500)

                    } catch (ex: Exception) {
                        Log.d(TAG, ex.message.toString())
                    }
                }

                if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    runOnUiThread { resetUiElements() }
                }
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }


    private fun saveData() {
        val list = listOf(
            networkData.slice(0 until EnvVariables.DESIRED_LENGTH).toByteArray(),
        )
        val result = ByteArrayListXOR.xor(list)
        val string = ByteArrayToBinaryStringConverter.convert(result)

        DataSaver.saveData(
            data = string,
            table = "network"
        )
    }

    private fun resetData() {
        networkData = ByteArray(0)
//        isDataGenerating = false
    }

    @SuppressLint("SetTextI18n")
    private fun resetUiElements() {
        binding.progressBarNetwork.progress = 0
        binding.txtNetworkPercent.text = "0%"
        binding.btnNetworkStart.text = "START"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi() {
        binding.progressBarNetwork.progress = smallestArraySize()
        binding.btnNetworkStart.text = if (isDataGenerating) "PAUSE" else "START"

        val percentage =
            (binding.progressBarNetwork.progress.toFloat() / binding.progressBarNetwork.max.toFloat()) * 100
        binding.txtNetworkPercent.text = "${percentage.toInt()}%"
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            networkData
        )
        return arrays.minBy { it.size }.size
    }
}