package com.example.data_processors

import com.example.utilities.Constants
import kotlin.experimental.and

object LongDataProcessor {
    /*
    This function takes a Long integer as input and returns a ByteArray containing the 8 least significant bits of the input value,
    represented as a single byte. If the input value is zero, it returns an empty ByteArray.
    The input value is first reduced modulo a constant prime value,
    and then the result is converted to a byte before extracting the least significant 8 bits.
     */
    fun getLeastSignificantByte(data: Long): ByteArray {
        if (data == 0L) {
            return ByteArray(0)
        }

        val modData = data % Constants.PRIME_FOR_MOD
        val dataInBytes = modData.toByte()

        return byteArrayOf(dataInBytes and 0xff.toByte())
    }
}