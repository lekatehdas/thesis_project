package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.TrafficStats
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.data_generator.NetworkTrafficDataGenerator
import com.example.pseudorandomgenerator.databinding.ActivityNetworkTrafficBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import kotlinx.coroutines.*

class NetworkTrafficActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkTrafficBinding

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

        val generator = NetworkTrafficDataGenerator(this)

        job = CoroutineScope(Job()).launch {
            while (isActive && generatedData.length < EnvVariables.DESIRED_LENGTH) {

                try {
                    val data = generator.networkTrafficVolume()
                    generatedData += generator.amplifyNetworkData(data)
                    runOnUiThread { updateProgressInUi() }
                    delay(1500)

                } catch (ex: Exception) {
                    Log.d(TAG, ex.message.toString())
                }
            }

            if (enoughData()) {
                println(generatedData)
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