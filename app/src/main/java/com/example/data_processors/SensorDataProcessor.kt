package com.example.data_processors

import android.hardware.SensorEvent
import kotlin.math.roundToLong

object SensorDataProcessor {
    /*
    All this was used in the first iteration of the program.

    fun reduceToByteWithXOR(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

        val byteList: List<ByteArray> = ListToByteArrayListConverter.convertFloatList(values.toList())
        val distilledBytesList = distillByteArrays(byteList)

        return ListDataProcessor.combineByteArraysByXOR(distilledBytesList)
    }

    private fun distillByteArrays(byteArrays: List<ByteArray>): List<ByteArray> {
        return byteArrays.map { array ->
            var result = array[0]
            for (i in 1 until array.size) {
                result = result xor array[i]
            }
            return@map byteArrayOf(result)
        }
    }

    fun combineFloatsToByteArrayWithXOR(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

        val byteList: List<ByteArray> = ListToByteArrayListConverter.convertFloatList(values.toList())

        return ListDataProcessor.combineByteArraysByXOR(byteList)
    }

    fun getLeastSignificantByte(event: SensorEvent): ByteArray {
        val values: FloatArray = event.values

        val average = (values.sum() / values.size)
        val fractional = getFractionalPartAsLong(average.toString())

        return LongDataProcessor.getLeastSignificantByte(fractional)
    }

    private fun getFractionalPartAsLong(data: String): Long {
        if (data.contains("E"))
            return data.filter { it.isDigit() }.toLong()

        return data.split(".")[1].toLong()
    }
     */
    fun xorToSingleNumber(event: SensorEvent): Long {
        val floatArray = event.values
        val longArray = floatArray.map { it.roundToLong() }

        // Perform XOR operation on all the values
        return longArray.reduce { acc, value -> acc xor value }
    }
}