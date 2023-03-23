package com.example.controllers

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Environment
import android.provider.MediaStore
import com.example.data_gatherers.MovementDataGatherer
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.runBlocking
import java.io.OutputStreamWriter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

class MovementActivityController(
    private val context: Context,
    private val updateUi: suspend (Int) -> Unit,
    private val resetUi: suspend () -> Unit
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val gatherer = MovementDataGatherer(context, ::onData)
    private val sensorDataHolder = DataHolder<FloatArray>()
    private val desiredLength = Constants.DESIRED_LENGTH

    fun start() {
        gatherer.start()
    }

    private fun onData(event: SensorEvent) {
        launch {
            val sensorName = event.sensor.getSensorName() ?: return@launch

            if (!sensorDataHolder.hasKey(sensorName)) {
                sensorDataHolder.createList(sensorName)
            }

            sensorDataHolder.addElementToList(sensorName, event.values.copyOf())

            if (sensorDataHolder.getListSize(sensorName) == desiredLength) {
                gatherer.unregisterSensor(event.sensor)
                saveData(event.sensor.type, sensorDataHolder.getListByName(sensorName).toMutableList())
            }

            if (sensorDataHolder.getAllKeys().all { key -> sensorDataHolder.getListSize(key) >= desiredLength }) {
                gatherer.start()
                sensorDataHolder.clearAllLists()
                (context as Activity).runOnUiThread { runBlocking { resetUi() } }
            }

            (context as Activity).runOnUiThread { runBlocking { updateUi(sensorDataHolder.getMinListSize()) } }
        }
    }

    private fun Sensor.getSensorName(): String? = when (type) {
        Sensor.TYPE_ACCELEROMETER -> "accelerationData"
        Sensor.TYPE_GYROSCOPE -> "gyroscopeData"
        Sensor.TYPE_MAGNETIC_FIELD -> "magnetometerData"
        Sensor.TYPE_ROTATION_VECTOR -> "rotationData"
        Sensor.TYPE_GRAVITY -> "gravityData"
        else -> null
    }

    private fun saveData(sensorType: Int, sensorData: MutableList<FloatArray>) {
        try {
            val fileName = "${System.currentTimeMillis()}.csv"
            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                put(MediaStore.Files.FileColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Movement/$sensorType/")
            }

            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    val writer = OutputStreamWriter(outputStream)
                    sensorData.forEach { event ->
                        if (event != null) {
                            writer.write(event.joinToString(","))
                            writer.write("\n")
                        }
                    }
                    writer.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}