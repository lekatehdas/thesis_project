package com.example.utilities

class DataHolder {
    private val bitLists: MutableMap<String, MutableList<Boolean>> = mutableMapOf()

    fun initializeArray(name: String) {
        bitLists[name] = mutableListOf()
    }

    fun addToList(name: String, value: Boolean) {
        if (bitLists[name] == null)
            return

        bitLists[name]!!.add(value)
    }

    fun getList(name: String): String {
        return bitListToString(bitLists[name]?.take(Constants.DESIRED_LENGTH) ?: listOf())
    }

    fun getAllLists(): List<String> {
        return bitLists.values.map { bitListToString(it.take(Constants.DESIRED_LENGTH)) }
    }

    fun resetData() {
        bitLists.forEach { (name, _) -> bitLists[name] = mutableListOf() }
    }

    fun getSizeOfSmallestList(): Int {
        return if (bitLists.isEmpty()) 0 else bitLists.values.minByOrNull { it.size }?.size ?: 0
    }

    fun getSizeOfAList(name: String): Int {
        return bitLists[name]?.size ?: 0
    }

    fun getArraysContainingTextSlicedToDesiredLength(text: String): List<String> {
        return bitLists.filterKeys { it.contains(text) }
            .values
            .map { bitListToString(it.take(Constants.DESIRED_LENGTH)) }
    }

    fun getArraysContainingText(text: String): List<String> {
        return bitLists.filterKeys { it.contains(text) }
            .values
            .map(::bitListToString)
    }

    private fun bitListToString(bitList: List<Boolean>): String {
        return bitList.joinToString(separator = "") { if (it) "1" else "0" }
    }
}
