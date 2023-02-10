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
import com.example.data_gatherers.NetworkTrafficDataGatherer
import com.example.data_processors.ByteArrayListXOR
import com.example.pseudorandomgenerator.databinding.ActivityNetworkBinding
import com.example.utilities.FirebaseDataSaver
import com.example.utilities.Constants
import com.example.data_processors.LeastSignificantBits
import com.example.utilities.DataHolder
import kotlinx.coroutines.*

class NetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkBinding
    private val network = "networkData"

    private lateinit var dataHolder: DataHolder
    private val sources = listOf(network)

    private var isDataGenerating = false
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasInternetConnection()) {
            Toast.makeText(this, "No internet available", Toast.LENGTH_SHORT).show()
            this.finish()
        }

        binding.progressBarNetwork.max = Constants.DESIRED_LENGTH
        initDataHolder()
        initListeners()
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        for (name in sources)
            dataHolder.initializeArray(name)
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnNetworkStart.setOnClickListener {
            if (isDataGenerating) {
                isDataGenerating = false
                job?.cancel()
                runOnUiThread { updateProgressInUi() }
            } else {
                startMethod()
                isDataGenerating = true
            }
        }
    }

    private fun startMethod() {
        val gatherer = NetworkTrafficDataGatherer()

        job = CoroutineScope(Job()).launch {
            while (isActive && dataHolder.getSizeOfSmallestArray() < Constants.DESIRED_LENGTH) {

                try {
                    val volume = gatherer.networkTrafficVolume()
                    val data = LeastSignificantBits.discardZerosGet8LSB(volume)

                    dataHolder.concatArray(network, data)

                    runOnUiThread { updateProgressInUi() }
                    delay(500)

                } catch (ex: Exception) {
                    Log.d(TAG, ex.message.toString())
                }
            }

            saveData()
            resetData()
            runOnUiThread { resetUiElements() }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }


    private fun saveData() {
        val results = dataHolder.getAllArrays()
        val result = ByteArrayListXOR.combineByteArraysThroughXOR(results)
        val string = ByteArrayToBinaryStringConverter.convert(result)

        FirebaseDataSaver.saveData(
            data = string,
            table = "network"
        )
    }

    private fun resetData() {
        dataHolder.resetData()
        isDataGenerating = false
    }

    @SuppressLint("SetTextI18n")
    private fun resetUiElements() {
        binding.progressBarNetwork.progress = 0
        binding.txtNetworkPercent.text = "0%"
        binding.btnNetworkStart.text = "START"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi() {
        binding.progressBarNetwork.progress = dataHolder.getSizeOfSmallestArray()
        binding.btnNetworkStart.text = if (isDataGenerating) "PAUSE" else "START"

        val percentage = (
                binding.progressBarNetwork.progress.toFloat() /
                        binding.progressBarNetwork.max.toFloat()) * 100
        binding.txtNetworkPercent.text = "${percentage.toInt()}%"
    }
}