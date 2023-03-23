package com.example.controllers

import android.util.Log
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_gatherers.NetworkTrafficDataGatherer
import com.example.data_processors.ListDataProcessor
import com.example.data_processors.LongDataProcessor
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver
import kotlinx.coroutines.*
import kotlin.reflect.KSuspendFunction0

class NetworkActivityController(
    private val dataHolder: DataHolder,
    private val network: String,
    private val updateProgress: KSuspendFunction0<Unit>,
    private val resetUi: KSuspendFunction0<Unit>
) {

    private val TAG = "NetworkTrafficDataCollector"
    private val gatherer = NetworkTrafficDataGatherer()
    private var job: Job? = null

    fun start() {
        job = CoroutineScope(Job()).launch {
            while (isActive && dataHolder.getSizeOfSmallestArray() < Constants.DESIRED_LENGTH) {

                try {
                    val volume = gatherer.networkTrafficVolume()
                    val data = LongDataProcessor.getLeastSignificantByte(volume)

                    dataHolder.concatArray(network, data)

                    updateProgress()
                    delay(500)

                } catch (ex: Exception) {
                    Log.d(TAG, ex.message.toString())
                }
            }

            if (dataHolder.getSizeOfSmallestArray() >= Constants.DESIRED_LENGTH) {
                saveData()
                resetData()
                resetUi()
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    private fun saveData() {
        val results = dataHolder.getAllArrays()
        val result = ListDataProcessor.combineByteArraysByXOR(results)
        val string = ByteArrayToBinaryStringConverter.convert(result)

        FirebaseDataSaver.saveData(
            data = string,
            table = "network"
        )
    }

    private fun resetData() {
        dataHolder.resetData()
    }
}
