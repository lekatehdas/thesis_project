package com.example.utilities

class DataHolder {
    private val byteArrays: MutableMap<String, ByteArray> = mutableMapOf()

    fun initializeArray(name: String) {
        byteArrays[name] = ByteArray(0)
    }

    fun concatArray(name: String, byteArray: ByteArray) {
        if (byteArrays[name] == null)
            return

        byteArrays[name] = byteArrays[name]!! + byteArray
    }

    fun getArray(name: String): ByteArray {
        return byteArrays[name]?.sliceArray(0 until Constants.DESIRED_LENGTH) ?: return ByteArray(0)
    }

    fun getAllArrays(): List<ByteArray> {
        return byteArrays.values.map { it.sliceArray(0 until Constants.DESIRED_LENGTH) }
    }

    fun resetData() {
        byteArrays.forEach { (name, _) -> byteArrays[name] = ByteArray(0) }
    }

    fun getSizeOfSmallestArray(): Int {
        return if (byteArrays.isEmpty()) { 0 } else { byteArrays.values.minBy { it.size }.size }
    }

    fun getSizeOfAnArray(name: String): Int {
        return if (byteArrays[name] == null) { 0 } else { byteArrays[name]!!.size }
    }

    fun getArraysContainingText(text: String): List<ByteArray> {
        return byteArrays.filterKeys { it.contains(text) }
            .values
            .map { it.sliceArray(0 until Constants.DESIRED_LENGTH) }
    }
}