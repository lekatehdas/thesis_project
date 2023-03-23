package com.example.data_processors

import android.hardware.SensorEvent

object SensorDataProcessor {
    fun getDataAsString(event: SensorEvent): String {
        val values: FloatArray = event.values
        val product = values.fold(1f) { acc, value -> acc * value }

        return product.toString()
    }
     */
    fun xorToSingleNumber(event: SensorEvent): Long {
        val floatArray = event.values
        val longArray = floatArray.map { it.roundToLong() }

        // Perform XOR operation on all the values
        return longArray.reduce { acc, value -> acc xor value }
    }
}