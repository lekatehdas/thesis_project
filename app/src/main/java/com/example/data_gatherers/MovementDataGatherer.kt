package com.example.data_gatherers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MovementDataGatherer(
    private val context: Context,
    private val onData: (SensorEvent) -> Unit
) : SensorEventListener {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val sensorMap: Map<Int, Sensor?> by lazy {
        listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_GRAVITY
        ).associateWith { sensorManager.getDefaultSensor(it) }
    }

    fun start() = registerListeners()

    fun stop() = unregisterAllListeners()

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { onData(it) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregisterSensor(sensor: Sensor) {
        sensorManager.unregisterListener(this, sensorMap[sensor.type])
    }


    private fun registerListeners() {
        sensorMap.forEach { (type, sensor) ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterAllListeners() {
        sensorManager.unregisterListener(this)
    }
}