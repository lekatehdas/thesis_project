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
import com.example.utilities.EnvVariables
import com.example.converters.NumberToByteArrayConverter
import com.example.utilities.DataSaver
import com.example.utilities.LeastSignificantBits

class MovementActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMovementBinding

    private var accelerationData = ByteArray(0)
    private var gyroscopeData = ByteArray(0)
    private var magnetometerData = ByteArray(0)
    private var rotationData = ByteArray(0)
    private var gravityData = ByteArray(0)
    private var timeData = ByteArray(0)

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null
    private var gravity: Sensor? = null

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
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
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
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
//            unregisterListeners()
            saveData()
            resetDataArrays()
            resetUiElements()
//            return
        }

        getDataFromEvent(event)
        updateUiElements()
    }

    private fun saveData() {
        val lists = listOf(
            accelerationData,
            gyroscopeData,
            magnetometerData,
            rotationData,
            gravityData
        )
        val xor = ByteArrayListXOR.xor(lists)

        val result = ByteArrayToBinaryStringConverter.convert(xor)
        DataSaver.saveData(
            data = result,
            table = "movement_LSB")

        val timeResult = ByteArrayToBinaryStringConverter.convert(timeData)
        DataSaver.saveData(
            data = timeResult,
            table = "time_alone")
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
        binding.progressBarMovement.progress = 0
    }

    private fun getDataFromEvent(event: SensorEvent) {
        if (timeData.size < EnvVariables.DESIRED_LENGTH) timeData += LeastSignificantBits.getSystemNanoTime()
        if (event.sensor.name.lowercase().contains("accelerometer")) accelerometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("rotation vector")) rotationVectorDataHandler(event)
        if (event.sensor.name.lowercase().contains("magnetometer")) magnetometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("gyroscope")) gyroscopeDataHandler(event)
        if (event.sensor.name.lowercase().contains("gravity")) gravityDataHandler(event)
    }

    private fun gravityDataHandler(event: SensorEvent) {
        if (gravityData.size == EnvVariables.DESIRED_LENGTH) {
            sensorManager.unregisterListener(this, gravity)
        } else {
            val byteArray = getBytes(event)
            gravityData += byteArray
        }
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
        gravityData = ByteArray(0)
        timeData = ByteArray(0)
        registerListeners()
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            accelerationData,
            gyroscopeData,
            magnetometerData,
            rotationData,
            gravityData,
            timeData
        )
        return arrays.minBy { it.size }.size ?: 0
    }

    private fun getBytes(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values
//
//        val byteList = NumberToByteArrayConverter.convertFloat(values.toList())
//
//        return ByteArrayListXOR.xor(byteList)

//        val values: LongArray = event.values.map { it.toLong() }.toLongArray()

        val average = (values.sum() / values.size)
        val fractional = getFractionalPartAsLong(average.toString())
        
        return LeastSignificantBits.modWithPrimeAndGet8LSB(fractional)
    }

    private fun getFractionalPartAsLong(data: String): Long {
        if (data.contains("E"))
            return data.filter { it.isDigit() }.toLong()

        return data.split(".")[1].toLong()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}