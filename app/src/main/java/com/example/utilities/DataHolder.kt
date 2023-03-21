package com.example.utilities

class DataHolder {
    private val bitLists: MutableMap<String, MutableList<Boolean>> = mutableMapOf()

    fun initializeList(name: String) {
        bitLists[name] = mutableListOf()
    }

    fun initializeLists(names: Collection<String>) {
        names.forEach { name -> initializeList(name) }
    }

    fun addToList(name: String, value: Boolean) {
        if (bitLists[name] == null)
            return

        bitLists[name]!!.add(value)
    }

    fun getList(name: String): String {
        return bitListToString(bitLists[name]?.take(Constants.DESIRED_LENGTH) ?: listOf())
    }

    fun resetData() {
        bitLists.forEach { (name, _) -> bitLists[name] = mutableListOf() }
    }

    fun getSizeOfSmallestList(): Int {
        return if (bitLists.isEmpty()) 0 else bitLists.values.minByOrNull { it.size }?.size ?: 0
    }

    fun getListSizeContainingText(text: String): Int {
        return bitLists.filterKeys { it.contains(text) }
            .values
            .size
    }

    private fun bitListToString(bitList: List<Boolean>): String {
        return bitList.joinToString(separator = "") { if (it) "1" else "0" }
    }
}
