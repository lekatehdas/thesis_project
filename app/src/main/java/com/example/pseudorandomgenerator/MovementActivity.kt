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
import com.example.converters.NumberToByteArrayConverter
import com.example.utilities.EnvVariables
import com.example.utilities.DataSaver
import com.example.utilities.LeastSignificantBits
import kotlin.experimental.xor

class MovementActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMovementBinding

    private val desiredLength = EnvVariables.DESIRED_LENGTH

    private var accelerationDataXOR = ByteArray(0)
    private var gyroscopeDataXOR = ByteArray(0)
    private var magnetometerDataXOR = ByteArray(0)
    private var rotationDataXOR = ByteArray(0)
    private var gravityDataXOR = ByteArray(0)

    private var accelerationDataLSB = ByteArray(0)
    private var gyroscopeDataLSB = ByteArray(0)
    private var magnetometerDataLSB = ByteArray(0)
    private var rotationDataLSB = ByteArray(0)
    private var gravityDataLSB = ByteArray(0)

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

        binding.progressBarMovement.max = desiredLength

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

        if (smallestArraySize() >= desiredLength) {
            unregisterListeners()
            saveData()
            resetDataArrays()
            resetUiElements()
            registerListeners()
//            return
        }

        getDataFromEvent(event)
        updateUiElements()
    }

    private fun saveData() {
        saveXOR()
        saveLSB()
        saveTime()
        saveEachDataArray()
    }

    private fun saveEachDataArray() {
        val list = listOf(
            Pair("movement_accelerationDataXOR", accelerationDataXOR),
            Pair("movement_gyroscopeDataXOR", gyroscopeDataXOR),
            Pair("movement_magnetometerDataXOR", magnetometerDataXOR),
            Pair("movement_rotationDataXOR", rotationDataXOR),
            Pair("movement_gravityDataXOR", gravityDataXOR),
            Pair("movement_accelerationDataLSB", accelerationDataLSB),
            Pair("movement_gyroscopeDataLSB", gyroscopeDataLSB),
            Pair("movement_magnetometerDataLSB", magnetometerDataLSB),
            Pair("movement_rotationDataLSB", rotationDataLSB),
            Pair("movement_gravityDataLSB", gravityDataLSB)
        )

        list.forEach { (table, data) ->
            DataSaver.saveData(
                table = table,
                data = ByteArrayToBinaryStringConverter.convert(data)
            )
        }
    }

    private fun saveTime() {
        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(timeData),
            table = "time_alone"
        )
    }

    private fun saveLSB() {
        val listOfLSB = listOf(
            accelerationDataLSB,
            gyroscopeDataLSB,
            magnetometerDataLSB,
            rotationDataLSB,
            gravityDataLSB
        )
        val lsbXOR = ByteArrayListXOR.xor(listOfLSB)
        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(lsbXOR),
            table = "movement_LSB"
        )
    }

    private fun saveXOR() {
        val listsOfXOR = listOf(
            accelerationDataXOR,
            gyroscopeDataXOR,
            magnetometerDataXOR,
            rotationDataXOR,
            gravityDataXOR
        )
        val xor = ByteArrayListXOR.xor(listsOfXOR)
        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(xor),
            table = "movement_XOR"
        )
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
        if (timeData.size < desiredLength) timeData += LeastSignificantBits.getSystemNanoTime()

        if (event.sensor.name.lowercase().contains("accelerometer")) accelerometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("rotation vector")) rotationVectorDataHandler(event)
        if (event.sensor.name.lowercase().contains("magnetometer")) magnetometerDataHandler(event)
        if (event.sensor.name.lowercase().contains("gyroscope")) gyroscopeDataHandler(event)
        if (event.sensor.name.lowercase().contains("gravity")) gravityDataHandler(event)
    }

    private fun gravityDataHandler(event: SensorEvent) {
        if (gravityDataXOR.size == desiredLength && gravityDataLSB.size == desiredLength) {
            sensorManager.unregisterListener(this, gravity)
        } else {
            gravityDataXOR += getBytesXORWay(event)
            gravityDataLSB += getBytesLSBWay(event)
        }
    }

    private fun gyroscopeDataHandler(event: SensorEvent) {
        if (gyroscopeDataXOR.size == desiredLength && gyroscopeDataLSB.size == desiredLength) {
            sensorManager.unregisterListener(this, gyroscope)
        } else {
            gyroscopeDataXOR += getBytesXORWay(event)
            gyroscopeDataLSB += getBytesLSBWay(event)
        }
    }

    private fun magnetometerDataHandler(event: SensorEvent) {
        if (magnetometerDataXOR.size == desiredLength && magnetometerDataLSB.size == desiredLength) {
            sensorManager.unregisterListener(this, magnetometer)
        } else {
            magnetometerDataXOR += getBytesXORWay(event)
            magnetometerDataLSB += getBytesLSBWay(event)
        }
    }

    private fun rotationVectorDataHandler(event: SensorEvent) {
        if (rotationDataXOR.size == desiredLength && rotationDataLSB.size == desiredLength) {
            sensorManager.unregisterListener(this, rotation)
        } else {
            rotationDataXOR += getBytesXORWay(event)
            rotationDataLSB += getBytesLSBWay(event)
        }
    }

    private fun accelerometerDataHandler(event: SensorEvent) {
        if (accelerationDataXOR.size == desiredLength && accelerationDataLSB.size == desiredLength) {
            sensorManager.unregisterListener(this, accelerometer)
        } else {
            accelerationDataXOR += getBytesXORWay(event)
            accelerationDataLSB += getBytesLSBWay(event)
        }
    }

    private fun resetDataArrays() {
        accelerationDataXOR = ByteArray(0)
        gyroscopeDataXOR = ByteArray(0)
        magnetometerDataXOR = ByteArray(0)
        rotationDataXOR = ByteArray(0)
        gravityDataXOR = ByteArray(0)

        accelerationDataLSB = ByteArray(0)
        gyroscopeDataLSB = ByteArray(0)
        magnetometerDataLSB = ByteArray(0)
        rotationDataLSB = ByteArray(0)
        gravityDataLSB = ByteArray(0)

        timeData = ByteArray(0)
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            accelerationDataXOR,
            gyroscopeDataXOR,
            magnetometerDataXOR,
            rotationDataXOR,
            gravityDataXOR,
            timeData,
            accelerationDataLSB,
            gyroscopeDataLSB,
            magnetometerDataLSB,
            rotationDataLSB,
            gravityDataLSB
        )
        return arrays.minBy { it.size }.size
    }

    private fun getBytesXORWay(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

        val byteList: List<ByteArray> = NumberToByteArrayConverter.convertFloat(values.toList())
        val distilledBytes = distillByteArrays(byteList)

        return ByteArrayListXOR.xor(distilledBytes)
    }

    fun distillByteArrays(byteArrays: List<ByteArray>): List<ByteArray> {
        return byteArrays.map { array ->
            var result = array[0]
            for (i in 1 until array.size) {
                result = result xor array[i]
            }
            return@map byteArrayOf(result)
        }
    }

    private fun getBytesLSBWay(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

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