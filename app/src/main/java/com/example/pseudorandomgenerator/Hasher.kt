package com.example.pseudorandomgenerator

import java.security.MessageDigest

class Hasher {
    companion object {
        fun stringHash(input: String): String {
            var data = ""
            val bytes = MessageDigest.getInstance("SHA-512").digest(input.toByteArray())

            for (value in bytes) {
                data += value.toString()
            }

            return data
        }
    }
}