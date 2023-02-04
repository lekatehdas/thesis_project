package com.example.converters

import java.nio.ByteBuffer

object NumberToByteArrayConverter {
    fun convertFloat(list: List<Float>): List<ByteArray> {
        return list.map { float ->
            val buffer = ByteBuffer.allocate(4)
            buffer.putFloat(float)
            buffer.array()
        }
    }
}