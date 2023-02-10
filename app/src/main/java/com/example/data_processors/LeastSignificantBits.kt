package com.example.data_processors

import com.example.utilities.Constants
import kotlin.experimental.and

object LeastSignificantBits {
    fun getSystemNanoTime(): ByteArray {
        val timeInLong = System.nanoTime()
        return modWithPrimeAndGet8LSB(timeInLong)
    }

    fun discardZerosGet8LSB(data: Long): ByteArray {
        if (data.toLong() == 0L) {
            return ByteArray(0)
        }

        val dataToLong = data.toLong()
        return modWithPrimeAndGet8LSB(dataToLong)
    }

    fun modWithPrimeAndGet8LSB(dataInLong: Long): ByteArray {
        val modData = dataInLong % Constants.PRIME_FOR_MOD

        val dataInBytes = modData.toByte()

        return byteArrayOf(dataInBytes and 0xff.toByte())
    }
}