package com.example.utilities

object StringTruncator {
    fun truncate(str: String, length: Int): String {
        return if (str.length > length) {
            str.substring(0, length)
        } else {
            str
        }
    }
}