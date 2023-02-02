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
            // This one takes the 4 LSB from n and n + 1 and combines them into a new byte.
            // newByteArray[i / 2] = (byteArray[i].toInt() and 0b0000_1111 shl 4 or (byteArray[i + 1].toInt() and 0b0000_1111)).toByte()
            newByteArray[i / 2] = (byteArray[i] xor byteArray[i + 1])
        }

        return newByteArray
    }
}