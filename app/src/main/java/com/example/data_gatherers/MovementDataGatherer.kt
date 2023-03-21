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

    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensors = mutableMapOf<Int, Sensor?>()

    init {
        initSensors()
    }

    fun start() {
        registerListeners()
    }

    fun stop() {
        unregisterListeners()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { onData(it) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregisterSensor(sensor: Sensor) {
        sensors[sensor.type]?.let { sensorManager.unregisterListener(this, it) }
    }

    private fun initSensors() {
        val sensorTypes = listOf(
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_GRAVITY
        )

        sensorTypes.forEach { type -> sensors[type] = sensorManager.getDefaultSensor(type) }
    }

    private fun registerListeners() {
        sensors.forEach { (type, sensor) ->
            sensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        }
    }

    private fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }
}
