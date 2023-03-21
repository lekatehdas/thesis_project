package com.example.data_processors

object LsbExtractor {
    fun long(data: Long): Boolean {
        val lsb = data and 1L

        return lsb == 1L
    }

    fun char(data: Char): Boolean {
        val lsb = data.code and 1

        return lsb == 1
    }
}