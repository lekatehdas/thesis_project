package com.example.pseudorandomgenerator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class MovementActivity : AppCompatActivity(),  SensorEventListener{
    private lateinit var btnStart: Button
    private lateinit var txtMovementPercent: TextView
    private lateinit var txtMovement: TextView
    private lateinit var progressBar: ProgressBar
    private var generatedData = ""

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rotation: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement)

        initViews()
        progressBar.max = EnvVariables.DESIRED_LENGTH

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
        txtMovement.text = ""

        btnStart.setOnClickListener {
            btnStart.text = "IN PROGRESS"
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
        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(this)
    }

    private fun initViews() {
        btnStart = findViewById(R.id.btnMovementStart)
        txtMovementPercent = findViewById(R.id.txtMovementPercent)
        txtMovement = findViewById(R.id.txtMovement)
        progressBar = findViewById(R.id.progressBarMovement)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (generatedData.length > EnvVariables.DESIRED_LENGTH) {
            unregisterListeners()
            btnStart.text = "START"
            generatedData = ""
            TODO("Do stuff with generated data")
        }

        if (event == null) {
            return
        }

        generatedData += getValues(event)
        progressBar.progress = generatedData.length
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