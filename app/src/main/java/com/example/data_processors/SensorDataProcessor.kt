package com.example.data_processors

import android.hardware.SensorEvent

object SensorDataProcessor {
    fun getDataAsString(event: SensorEvent): String {
        val values: FloatArray = event.values
        val product = values.fold(1f) { acc, value -> acc * value }

        return product.toString()
    }
}