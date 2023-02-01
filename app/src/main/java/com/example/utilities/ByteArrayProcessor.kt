package com.example.utilities

import kotlin.experimental.xor

class ByteArrayProcessor {
    fun process(input: ByteArray): ByteArray {
        var byteArray = input
        if (byteArray.size % 2 != 0) {
            byteArray = byteArray.copyOfRange(1, byteArray.size)
        }

        val newByteArray = ByteArray(byteArray.size / 2)
        for (i in byteArray.indices step 2) {
            newByteArray[i / 2] = (byteArray[i] xor byteArray[i + 1])
        }

        return newByteArray
    }
}