package com.example.data_gatherers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MovementDataGatherer(
    private val context: Context,
    private val onData: (SensorEvent) -> Unit
): SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null
    private var gravity: Sensor? = null

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
        if (event == null) return
        onData(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun unregisterSensor(sensor: Sensor) {
        if (sensor == accelerometer) sensorManager.unregisterListener(this, accelerometer)
        if (sensor == rotation) sensorManager.unregisterListener(this, rotation)
        if (sensor == magnetometer) sensorManager.unregisterListener(this, magnetometer)
        if (sensor == gyroscope) sensorManager.unregisterListener(this, gyroscope)
        if (sensor == gravity) sensorManager.unregisterListener(this, gravity)
    }

    private fun initSensors() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    private fun registerListeners() {
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }
}