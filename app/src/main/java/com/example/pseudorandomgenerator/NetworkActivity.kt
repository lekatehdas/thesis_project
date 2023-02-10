package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.collectors.NetworkDataCollector
import com.example.pseudorandomgenerator.databinding.ActivityNetworkBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.*

class NetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkBinding
    private val network = "networkData"

    private lateinit var dataHolder: DataHolder
    private val sources = listOf(network)

    private lateinit var collector: NetworkDataCollector

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
        initCollector()
        initListeners()
    }

    private fun initCollector() {
        collector = NetworkDataCollector(
            dataHolder,
            network,
            ::updateProgressInUi,
            ::resetUiElements
        )
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        for (name in sources)
            dataHolder.initializeArray(name)
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnNetworkStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                collector.start()
            if (!isChecked)
                collector.stop()
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    @SuppressLint("SetTextI18n")
    private suspend fun resetUiElements() {
        withContext(Dispatchers.Main) {
            binding.progressBarNetwork.progress = 0
            binding.txtNetworkPercent.text = "0%"
            binding.btnNetworkStart.text = "START"
            binding.btnNetworkStart.isChecked = false
        }

    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateProgressInUi() {
        withContext(Dispatchers.Main) {
            binding.progressBarNetwork.progress = dataHolder.getSizeOfSmallestArray()

            val percentage = (
                    binding.progressBarNetwork.progress.toFloat() /
                            binding.progressBarNetwork.max.toFloat()) * 100
            binding.txtNetworkPercent.text = "${percentage.toInt()}%"
        }
    }
}