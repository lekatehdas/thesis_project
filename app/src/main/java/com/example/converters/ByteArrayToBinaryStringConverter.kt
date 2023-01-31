package com.example.converters

object ByteArrayToBinaryStringConverter {
    fun convert(byteArray: ByteArray): String {
        return byteArray.joinToString("") {
            String.format("%8s", Integer.toBinaryString(it.toInt() and 0xff)).replace(' ', '0')
        }
    }
}