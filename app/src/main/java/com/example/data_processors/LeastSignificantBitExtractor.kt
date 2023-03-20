package com.example.data_processors

object LeastSignificantBitExtractor {
    fun extract(data: Double): Boolean {
        val lsb = data.toRawBits() and 1

        return lsb == 1L
    }
}