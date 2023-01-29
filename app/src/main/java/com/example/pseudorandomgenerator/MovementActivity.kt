package com.example.pseudorandomgenerator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityMovementBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables

class MovementActivity : AppCompatActivity(),  SensorEventListener{
    private lateinit var binding: ActivityMovementBinding

    private var generatedData = ""

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun initListeners() {
        binding.txtMovement.text = ""

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
        if (generatedData.length > EnvVariables.DESIRED_LENGTH) {
            unregisterListeners()
            saveData()
            binding.btnMovementStart.text = "START"
            generatedData = ""
        }

        if (event == null) {
            return
        }

        generatedData += getValues(event)
        binding.progressBarMovement.progress = generatedData.length
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "movement"
        )
    }

    private fun getValues(event: SensorEvent): String {
        val stringBuilder = StringBuilder()
        for (value in event.values) {
            stringBuilder.append(value.toString())
        }
        return stringBuilder.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}