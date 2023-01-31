package com.example.utilities

import kotlin.experimental.and

object NanoTimeLeastSignificantEight {
    fun getTimeInLower8Bits(): ByteArray {
        val timeInLong = System.nanoTime()
        val modTime = timeInLong % EnvVariables.PRIME_FOR_MOD

        val timeInBytes = modTime.toByte()

        return byteArrayOf(timeInBytes and 0xff.toByte())
    }
}