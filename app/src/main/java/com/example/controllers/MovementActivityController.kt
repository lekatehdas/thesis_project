package com.example.controllers

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import com.example.data_gatherers.MovementDataGatherer
import com.example.data_processors.LsbExtractor
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
    private val gatherer = MovementDataGatherer(context, ::onData)

    private val sensorSources = mapOf(
        Sensor.TYPE_ACCELEROMETER to "accelerationData",
        Sensor.TYPE_ROTATION_VECTOR to "rotationData",
        Sensor.TYPE_MAGNETIC_FIELD to "magnetometerData",
        Sensor.TYPE_GYROSCOPE to "gyroscopeData",
        Sensor.TYPE_GRAVITY to "gravityData"
    )

    init {
        dataHolder.initializeLists(sensorSources.values)
    }

    fun start() {
        gatherer.start()
    }

    private fun onData(event: SensorEvent) {
        val sensorType = event.sensor.type
        val sensorName = sensorSources[sensorType] ?: return

        if (dataHolder.getSizeOfSmallestList() >= Constants.DESIRED_LENGTH) {
            processFullData()
        } else {
            sensorDataHandler(sensorName, event)
        }
    }

    private fun sensorDataHandler(sensorName: String, event: SensorEvent) {
        if (enoughData(dataHolder.getListSizeContainingText(sensorName))) {
            gatherer.unregisterSensor(event.sensor)
        } else {
            addToDataHolder(sensorName, event)
            (context as Activity).runOnUiThread { runBlocking { updateUi(dataHolder.getSizeOfSmallestList()) } }
        }
    }

    private fun addToDataHolder(sensorName: String, event: SensorEvent) {
        val longNumber = SensorDataProcessor.xorToSingleNumber(event)
        val bit = LsbExtractor.long(longNumber)
        dataHolder.addToList(sensorName, bit)
    }

    private fun enoughData(size: Int): Boolean {
        return size >= Constants.DESIRED_LENGTH
    }

    private fun processFullData() {
        gatherer.stop()
        saveData()
        dataHolder.resetData()

        (context as Activity).runOnUiThread { runBlocking { resetUi() } }
        gatherer.start() //TODO This is only for infinite loop, to make the data generating easier.
    }

    private fun saveData() {
        val list = listOf(
            Pair("movement_acceleration", dataHolder.getList("accelerationData")),
            Pair("movement_gyroscope", dataHolder.getList("gyroscopeData")),
            Pair("movement_magnetometer", dataHolder.getList("magnetometerData")),
            Pair("movement_rotation", dataHolder.getList("rotationData")),
            Pair("movement_gravity", dataHolder.getList("gravityData"))
        )

        list.forEach { (table, data) ->
            FirebaseDataSaver.saveData(data, table)
        }
    }
}
