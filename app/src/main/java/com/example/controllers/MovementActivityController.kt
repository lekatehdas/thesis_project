package com.example.controllers

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import com.example.data_gatherers.MovementDataGatherer
import com.example.data_processors.LeastSignificantBitExtractor
import com.example.data_processors.SensorDataProcessor
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver
import kotlinx.coroutines.runBlocking

class MovementActivityController(
    private val context: Context,
    private val updateUi: suspend (Int) -> Unit,
    private val resetUi: suspend () -> Unit
) {

    private val dataHolder = DataHolder()
    private val gatherer = MovementDataGatherer(
        context,
        ::onData
    )

    private val accelerationData = "accelerationData"
    private val gyroscopeData = "gyroscopeData"
    private val magnetometerData = "magnetometerData"
    private val rotationData = "rotationData"
    private val gravityData = "gravityData"
    private val sources = listOf(
        accelerationData,
        gyroscopeData,
        magnetometerData,
        rotationData,
        gravityData
    )

    init {
        initDataHolder()
    }

    private fun initDataHolder() {
        for (source in sources) {
            dataHolder.initializeList(source)
        }
    }

    fun start() {
        gatherer.start()
    }

    private fun onData(event: SensorEvent) {
        if (dataHolder.getSizeOfSmallestList() >= Constants.DESIRED_LENGTH) {
            gatherer.stop()
            saveData()
            resetData()

            (context as Activity).runOnUiThread { runBlocking { resetUi } }
            gatherer.start() //TODO This is only for infinite loop, to make the data generating easier.
        } else {
            getDataFromEvent(event)
            (context as Activity).runOnUiThread { runBlocking { updateUi(dataHolder.getSizeOfSmallestList()) } }
        }
    }

    private fun getDataFromEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerDataHandler(event)
            Sensor.TYPE_ROTATION_VECTOR -> rotationVectorDataHandler(event)
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerDataHandler(event)
            Sensor.TYPE_GYROSCOPE -> gyroscopeDataHandler(event)
            Sensor.TYPE_GRAVITY -> gravityDataHandler(event)
        }
    }

    private fun gravityDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(gravityData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            val longNumber = SensorDataProcessor.xorToSingleNumber(event)
            val bit = LeastSignificantBitExtractor.extract(longNumber)
            dataHolder.addToList(gravityData, bit)
        }
    }

    private fun gyroscopeDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(gyroscopeData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            val longNumber = SensorDataProcessor.xorToSingleNumber(event)
            val bit = LeastSignificantBitExtractor.extract(longNumber)
            dataHolder.addToList(gyroscopeData, bit)
        }
    }

    private fun magnetometerDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(magnetometerData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            val longNumber = SensorDataProcessor.xorToSingleNumber(event)
            val bit = LeastSignificantBitExtractor.extract(longNumber)
            dataHolder.addToList(magnetometerData, bit)
        }
    }

    private fun rotationVectorDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(rotationData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            val longNumber = SensorDataProcessor.xorToSingleNumber(event)
            val bit = LeastSignificantBitExtractor.extract(longNumber)
            dataHolder.addToList(rotationData, bit)
        }
    }

    private fun accelerometerDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(accelerationData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            val longNumber = SensorDataProcessor.xorToSingleNumber(event)
            val bit = LeastSignificantBitExtractor.extract(longNumber)
            dataHolder.addToList(accelerationData, bit)
        }
    }

    private fun enoughData(size: Int): Boolean {
        return size >= Constants.DESIRED_LENGTH
    }

    private fun saveData() {
        saveEachDataArray()
    }

    private fun resetData() {
        dataHolder.resetData()
    }

    private fun saveEachDataArray() {
        val list = listOf(
            Pair("movement_acceleration", dataHolder.getList(accelerationData)),
            Pair("movement_gyroscope", dataHolder.getList(gyroscopeData)),
            Pair("movement_magnetometer", dataHolder.getList(magnetometerData)),
            Pair("movement_rotation", dataHolder.getList(rotationData)),
            Pair("movement_gravity", dataHolder.getList(gravityData))
        )

        list.forEach { (table, data) ->
            FirebaseDataSaver.saveData(
                table = table,
                data = data
            )
        }
    }
}