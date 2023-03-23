package com.example.utilities

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

object FirebaseDataSaver {
    fun saveData(data: Any, table: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference(table)
        dbRef.push().setValue(data)

        val incrementRef = FirebaseDatabase.getInstance().getReference("count")
        incrementRef.setValue(ServerValue.increment(1))
    }
}