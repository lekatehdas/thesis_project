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

    private var numUnregisteredSensors = 0
    private fun onData(event: SensorEvent) {
        launch {
            val sensorName = event.sensor.getSensorName() ?: return@launch

            if (!sensorDataHolder.hasKey(sensorName)) {
                sensorDataHolder.createList(sensorName)
            }

            sensorDataHolder.addElementToList(sensorName, event.values.copyOf())

            if (sensorDataHolder.getListSize(sensorName) == desiredLength) {
                saveData(
                    event.sensor.getSensorName()!!,
                    sensorDataHolder.getListByName(sensorName).toMutableList()
                )
                numUnregisteredSensors++
                gatherer.unregisterSensor(event.sensor.type)
            }

            if (numUnregisteredSensors == sensorDataHolder.getAllKeys().size) {
                sensorDataHolder.clearAllLists()
                gatherer.start()
                numUnregisteredSensors = 0
                (context as Activity).runOnUiThread { runBlocking { resetUi() } }
            }

            (context as Activity).runOnUiThread { runBlocking { updateUi(sensorDataHolder.getMinListSize()) } }
        }
    }


    private fun Sensor.getSensorName(): String? = when (type) {
        Sensor.TYPE_ACCELEROMETER -> "acceleration"
        Sensor.TYPE_GYROSCOPE -> "gyroscope"
        Sensor.TYPE_MAGNETIC_FIELD -> "magnetometer"
        Sensor.TYPE_ROTATION_VECTOR -> "rotation"
        Sensor.TYPE_GRAVITY -> "gravity"
        else -> null
    }

    private fun saveData(sensorName: String, sensorData: MutableList<FloatArray>) {
        launch(Dispatchers.IO) {
            try {
                val fileName = "${System.currentTimeMillis()}.csv"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                    put(
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Movement/$sensorName/"
                    )
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    contentValues
                )
                uri?.let {
                    context.contentResolver.openOutputStream(uri).use { outputStream ->
                        val writer = OutputStreamWriter(outputStream)
                        sensorData.forEach { event ->
                            writer.write(event.joinToString(","))
                            writer.write("\n")
                        }
                        writer.flush()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}