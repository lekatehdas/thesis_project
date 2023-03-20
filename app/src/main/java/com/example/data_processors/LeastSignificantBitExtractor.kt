package com.example.data_processors

object LeastSignificantBitExtractor {
    fun extract(data: Long): Boolean {
        val lsb = data and 1

        return lsb == 1L
    }
}