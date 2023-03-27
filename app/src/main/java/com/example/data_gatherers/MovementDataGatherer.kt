package com.example.data_gatherers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MovementDataGatherer(
    private val context: Context,
    private val onData: (SensorEvent) -> Unit
) {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(5)

    private val sensorTypes = listOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_MAGNETIC_FIELD,
        Sensor.TYPE_ROTATION_VECTOR,
        Sensor.TYPE_GRAVITY
    )

    private val sensorEventListeners = sensorTypes.map { createSensorEventListener() }

    private fun createSensorEventListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let { eventData ->
                    executorService.submit { onData(eventData) }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    fun start() = registerListeners()

    fun stop() = unregisterAllListeners()

    private fun registerListeners() {
        sensorTypes.forEachIndexed { index, type ->
            val sensor = sensorManager.getDefaultSensor(type)
            sensor?.let {
                sensorManager.registerListener(
                    sensorEventListeners[index],
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    fun unregisterSensor(sensorType: Int) {
        val index = sensorTypes.indexOf(sensorType)
        if (index != -1) {
            val sensor = sensorManager.getDefaultSensor(sensorType)
            sensor?.let {
                sensorManager.unregisterListener(sensorEventListeners[index], sensor)
            }
        }
    }

    private fun unregisterAllListeners() {
        sensorTypes.forEachIndexed { index, type ->
            val sensor = sensorManager.getDefaultSensor(type)
            sensor?.let {
                sensorManager.unregisterListener(sensorEventListeners[index], sensor)
            }
        }
    }
}
