package com.example.converters

import java.nio.ByteBuffer

object ListToByteArrayListConverter {
    fun convertFloatList(list: List<Float>): List<ByteArray> {
        return list.map { float ->
            val buffer = ByteBuffer.allocate(4)
            buffer.putFloat(float)
            buffer.array()
        }
    }
}