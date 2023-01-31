package com.example.utilities

object NanoTimeLeastSignificantEight {
    fun getTimeInLower8Bits(): ByteArray {
        val timeInFloat = System.nanoTime() / 1000000000.0f
        val timeInInt = java.lang.Float.floatToIntBits(timeInFloat)
        val lower8Bits = timeInInt and 0xff

        return byteArrayOf(lower8Bits.toByte())
    }
}