package com.example.data_processors

import kotlin.experimental.xor

object ListDataProcessor {
    /*
    This function takes a list of byte arrays as input and combines them through a bitwise XOR operation.
    It returns a new byte array that contains the result of the XOR operation.
    The function iterates over each byte array in the list and applies the XOR operation to the corresponding bytes of the result array.
     */
    fun combineByteArraysByXOR(byteArrays: List<ByteArray>): ByteArray {
        val result = byteArrays[0].clone()
        for (i in 1 until byteArrays.size) {
            for (j in result.indices) {
                result[j] = result[j] xor byteArrays[i][j]
            }
        }
        return result
    }
}