package com.example.utilities

import kotlin.experimental.and

object NanoTimeLeastSignificantEight {
    fun getTimeInLower8Bits(): ByteArray {
        val timeInLong = System.nanoTime()
        val modTime = timeInLong % 524287

        val timeInBytes = modTime.toByte()

        return byteArrayOf(timeInBytes and 0xff.toByte())
    }
}