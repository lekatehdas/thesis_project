package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityMovementBinding
import com.example.utilities.ByteArrayListXOR
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.utilities.DataSaver.saveData
import com.example.utilities.EnvVariables
import com.example.converters.FloatToByteArrayConverter

class MovementActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMovementBinding

    private var accelerationData = ByteArray(0)
    private var gyroscopeData = ByteArray(0)
    private var magnetometerData = ByteArray(0)
    private var rotationData = ByteArray(0)

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null
    private var decibel: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarMovement.max = EnvVariables.DESIRED_LENGTH

        initSensors()
        initListeners()

    }

    private fun initSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        decibel = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private fun initListeners() {
        binding.btnMovementStart.setOnClickListener {
            binding.btnMovementStart.text = "IN PROGRESS"
            registerListeners()
        }
    }

    private fun registerListeners() {
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
            unregisterListeners()
            val result = ByteArrayListXOR.xor(listOf(
                    accelerationData,
                    gyroscopeData,
                    magnetometerData,
                    rotationData
                ))
            val resultString = ByteArrayToBinaryStringConverter.convert(result)
            saveData(resultString, "movement")

            resetDataArrays()
            resetUiElements()
            return
        }

        if (event == null) {
            return
        }

        getDataFromEvent(event)
        updateUiElements()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        binding.progressBarMovement.progress = smallestArraySize()
        val percentage =
            (binding.progressBarMovement.progress.toFloat() / binding.progressBarMovement.max.toFloat()) * 100
        binding.txtMovementPercent.text = "${percentage.toInt()}%"
    }

    private fun resetUiElements() {
        binding.btnMovementStart.text = "START"
        binding.txtMovementPercent.text = "0%"
        binding.progressBarMovement.progress = smallestArraySize()
    }

    private fun getDataFromEvent(event: SensorEvent) {
        if (event.sensor.name.lowercase().contains("accelerometer")) accelerometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("rotation vector")) rotationVectorDataHandler(event)
        if (event.sensor.name.lowercase().contains("magnetometer")) magnetometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("gyroscope")) gyroscopeDataHandler(event)
    }

    private fun gyroscopeDataHandler(event: SensorEvent) {
        if (gyroscopeData.size == EnvVariables.DESIRED_LENGTH) {
            sensorManager.unregisterListener(this, gyroscope)
        } else {
            val byteArray = getBytes(event)
            gyroscopeData += byteArray
        }
    }

    private fun magnetometerDataHandler(event: SensorEvent) {
        if (magnetometerData.size == EnvVariables.DESIRED_LENGTH) {
            sensorManager.unregisterListener(this, magnetometer)
        } else {
            val byteArray = getBytes(event)
            magnetometerData += byteArray
        }
    }

    private fun rotationVectorDataHandler(event: SensorEvent) {
        if (rotationData.size == EnvVariables.DESIRED_LENGTH) {
            sensorManager.unregisterListener(this, rotation)
        } else {
            val byteArray = getBytes(event)
            rotationData += byteArray
        }
    }

    private fun accelerometerDataHandler(event: SensorEvent) {
        if (accelerationData.size == EnvVariables.DESIRED_LENGTH) {
            sensorManager.unregisterListener(this, accelerometer)
        } else {
            val byteArray = getBytes(event)
            accelerationData += byteArray
        }
    }

    private fun resetDataArrays() {
        accelerationData = ByteArray(0)
        gyroscopeData = ByteArray(0)
        magnetometerData = ByteArray(0)
        rotationData = ByteArray(0)
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            accelerationData,
            gyroscopeData,
            magnetometerData,
            rotationData
        )
        return arrays.minBy { it.size }.size ?: 0
    }

    private fun getBytes(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

        val byteList = FloatToByteArrayConverter.convert(values.toList())

        return ByteArrayListXOR.xor(byteList)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}