package com.example.utilities

class DataHolder<T> {
    private val dataMap: MutableMap<String, MutableList<T>> = mutableMapOf()

    fun createList(name: String) {
        dataMap[name] = mutableListOf()
    }

    fun addElementToList(name: String, data: T) {
        if (dataMap[name] == null)
            return

        dataMap[name]?.add(data)
    }

    fun getListByName(name: String): List<T> {
        return dataMap[name]?.toList() ?: emptyList()
    }

    fun clearAllLists() {
        dataMap.forEach { (name, _) -> dataMap[name] = mutableListOf() }
    }

    fun clearList(name: String) {
        dataMap[name] = mutableListOf()
    }

    fun getMinListSize(): Int {
        return dataMap.values.minOfOrNull { it.size } ?: 0
    }

    fun getListSize(name: String): Int {
        return dataMap[name]?.size ?: 0
    }

    fun hasKey(name: String): Boolean {
        return dataMap.containsKey(name)
    }

    fun getAllKeys(): Set<String> {
        return dataMap.keys
    }
}
