package com.example.utilities

import kotlin.experimental.xor

object ByteArrayListXOR {
    fun xor(byteArrays: List<ByteArray>): ByteArray {
        val result = byteArrays[0].clone()
        for (i in 1 until byteArrays.size) {
            for (j in result.indices) {
                result[j] = result[j] xor byteArrays[i][j]
            }
        }
        return result
    }
}