package com.example.controllers

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_gatherers.MovementDataGatherer
import com.example.data_processors.ListDataProcessor
import com.example.data_processors.SensorDataProcessor.getLeastSignificantByte
import com.example.data_processors.SensorDataProcessor.combineFloatsToByteArrayWithXOR
import com.example.data_processors.SensorDataProcessor.reduceToByteWithXOR
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver
import kotlinx.coroutines.runBlocking

class MovementActivityController(
    private val context: Context,
    private val updateUi: suspend (Int) -> Unit,
    private val resetUi: suspend () -> Unit
) {

    private val dataHolder = DataHolder()
    private val gatherer = MovementDataGatherer(
        context,
        ::onData
    )

    private val accelerationData = "accelerationData"
    private val gyroscopeData = "gyroscopeData"
    private val magnetometerData = "magnetometerData"
    private val rotationData = "rotationData"
    private val gravityData = "gravityData"
    private val sources = listOf(
        accelerationData,
        gyroscopeData,
        magnetometerData,
        rotationData,
        gravityData
    )

    private val xorOld = "_xor_old"
    private val lsb = "_lsb"
    private val xor = "_xor"
    private val affixes = listOf(
        xorOld,
        lsb,
        xor
    )


    init {
        initDataHolder()
    }

    private fun initDataHolder() {
        for (source in sources) {
            for (affix in affixes) {
                dataHolder.initializeArray(source + affix)
            }
        }
    }

    fun start() {
        gatherer.start()
    }

    private fun onData(event: SensorEvent) {
        if (dataHolder.getSizeOfSmallestList() >= Constants.DESIRED_LENGTH) {
            gatherer.stop()
            saveData()
            resetData()

            (context as Activity).runOnUiThread { runBlocking { resetUi } }
            gatherer.start() //TODO This is only for infinite loop, to make the data generating easier.
        } else {
            getDataFromEvent(event)
            (context as Activity).runOnUiThread { runBlocking { updateUi(dataHolder.getSizeOfSmallestList()) } }
        }
    }

    private fun getDataFromEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerDataHandler(event)
            Sensor.TYPE_ROTATION_VECTOR -> rotationVectorDataHandler(event)
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerDataHandler(event)
            Sensor.TYPE_GYROSCOPE -> gyroscopeDataHandler(event)
            Sensor.TYPE_GRAVITY -> gravityDataHandler(event)
        }
    }

    private fun gravityDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getArraysContainingText(gravityData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            dataHolder.addToList(gravityData + xorOld, combineFloatsToByteArrayWithXOR(event))
            dataHolder.addToList(gravityData + xor, reduceToByteWithXOR(event))
            dataHolder.addToList(gravityData + lsb, getLeastSignificantByte(event))
        }
    }

    private fun gyroscopeDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getArraysContainingText(gyroscopeData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            dataHolder.addToList(gyroscopeData + xorOld, combineFloatsToByteArrayWithXOR(event))
            dataHolder.addToList(gyroscopeData + xor, reduceToByteWithXOR(event))
            dataHolder.addToList(gyroscopeData + lsb, getLeastSignificantByte(event))
        }
    }

    private fun magnetometerDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getArraysContainingText(magnetometerData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            dataHolder.addToList(magnetometerData + xorOld, combineFloatsToByteArrayWithXOR(event))
            dataHolder.addToList(magnetometerData + xor, reduceToByteWithXOR(event))
            dataHolder.addToList(magnetometerData + lsb, getLeastSignificantByte(event))
        }
    }

    private fun rotationVectorDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getArraysContainingText(rotationData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            dataHolder.addToList(rotationData + xorOld, combineFloatsToByteArrayWithXOR(event))
            dataHolder.addToList(rotationData + xor, reduceToByteWithXOR(event))
            dataHolder.addToList(rotationData + lsb, getLeastSignificantByte(event))
        }
    }

    private fun accelerometerDataHandler(event: SensorEvent) {
        if (enoughData(dataHolder.getArraysContainingText(accelerationData))) {
            gatherer.unregisterSensor(event.sensor)

        } else {
            dataHolder.addToList(accelerationData + xorOld, combineFloatsToByteArrayWithXOR(event))
            dataHolder.addToList(accelerationData + xor, reduceToByteWithXOR(event))
            dataHolder.addToList(accelerationData + lsb, getLeastSignificantByte(event))
        }
    }

    private fun enoughData(list: List<ByteArray>): Boolean {
        return list.all { it.size >= Constants.DESIRED_LENGTH }
    }

    private fun saveData() {
        saveXOR()
        saveXOROld()
        saveLSB()
        saveEachDataArray()
    }

    private fun resetData() {
        dataHolder.resetData()
    }

    private fun saveXOROld() {
        val listsOfXOR = dataHolder.getArraysContainingTextSlicedToDesiredLength(xorOld)
        val xor = ListDataProcessor.combineByteArraysByXOR(listsOfXOR)

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(xor),
            table = "movement_XOR_old_way"
        )
    }

    private fun saveEachDataArray() {
        val list = listOf(
            Pair("movement_accelerationDataXOR", dataHolder.getList(accelerationData + xor)),
            Pair("movement_gyroscopeDataXOR", dataHolder.getList(gyroscopeData + xor)),
            Pair("movement_magnetometerDataXOR", dataHolder.getList(magnetometerData + xor)),
            Pair("movement_rotationDataXOR", dataHolder.getList(rotationData + xor)),
            Pair("movement_gravityDataXOR", dataHolder.getList(gravityData + xor)),
            Pair("movement_accelerationDataLSB", dataHolder.getList(accelerationData + lsb)),
            Pair("movement_gyroscopeDataLSB", dataHolder.getList(gyroscopeData + lsb)),
            Pair("movement_magnetometerDataLSB", dataHolder.getList(magnetometerData + lsb)),
            Pair("movement_rotationDataLSB", dataHolder.getList(rotationData + lsb)),
            Pair("movement_gravityDataLSB", dataHolder.getList(gravityData + lsb))
        )

        list.forEach { (table, data) ->
            FirebaseDataSaver.saveData(
                table = table,
                data = ByteArrayToBinaryStringConverter.convert(data)
            )
        }
    }

    private fun saveLSB() {
        val listOfLSB = dataHolder.getArraysContainingTextSlicedToDesiredLength(lsb)
        val lsbXOR = ListDataProcessor.combineByteArraysByXOR(listOfLSB)

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(lsbXOR),
            table = "movement_LSB"
        )
    }

    private fun saveXOR() {
        val listsOfXOR = dataHolder.getArraysContainingTextSlicedToDesiredLength(xor)
        val xor = ListDataProcessor.combineByteArraysByXOR(listsOfXOR)

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(xor),
            table = "movement_XOR"
        )
    }
}