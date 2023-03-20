package com.example.data_processors

import kotlin.experimental.and

object LeastSignificantBitExtractor {
    inline fun <reified T : Number> extract(data: T): Boolean {
        val lsb = when (T::class) {
            Long::class -> (data as Long) and 1
            Int::class -> (data as Int) and 1
            Short::class -> (data as Short) and 1
            Byte::class -> (data as Byte) and 1
            else -> throw IllegalArgumentException("Unsupported type")
        }

        return lsb == 1
    }

    fun extract(data: Char): Boolean {
        val lsb = data.code and 1

        return lsb == 1
    }
}

