package com.example.data_gatherers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MovementDataGatherer(
    private val context: Context,
    private val onData: (SensorEvent) -> Unit
) {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensorTypes = listOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_MAGNETIC_FIELD,
        Sensor.TYPE_ROTATION_VECTOR,
        Sensor.TYPE_GRAVITY
    )

    private val sensorEventListener = createSensorEventListener()

    @OptIn(DelicateCoroutinesApi::class)
    val dedicatedThreads: Map<Int, CoroutineContext> = sensorTypes.associateWith { type ->
        newSingleThreadContext("SensorThread-$type")
    }

    private fun createSensorEventListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let { eventData ->
                    onData(eventData)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    fun start() = registerListeners()

    fun stop() = unregisterAllListeners()

    private fun registerListeners() {
        sensorTypes.forEach { type ->
            val sensor = sensorManager.getDefaultSensor(type)
            sensor?.let {
                sensorManager.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    fun unregisterSensor(sensorType: Int) {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        sensor?.let {
            sensorManager.unregisterListener(sensorEventListener, sensor)
        }
    }

    private fun unregisterAllListeners() {
        sensorTypes.forEach { type ->
            val sensor = sensorManager.getDefaultSensor(type)
            sensor?.let {
                sensorManager.unregisterListener(sensorEventListener, sensor)
            }
        }
    }

    fun closeDedicatedThreads() {
        dedicatedThreads.values.forEach { coroutineContext ->
            coroutineContext.cancel()
        }
    }
}