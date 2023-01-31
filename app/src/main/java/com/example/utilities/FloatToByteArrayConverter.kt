package com.example.utilities

import java.nio.ByteBuffer

object FloatToByteArrayConverter {
    /**
     * @param list of Floats
     * @return list of ByteArrays
     */
    fun convert(list: List<Float>): List<ByteArray> {
        return list.map { float ->
            val buffer = ByteBuffer.allocate(4)
            buffer.putFloat(float)
            buffer.array()
        }
    }
}