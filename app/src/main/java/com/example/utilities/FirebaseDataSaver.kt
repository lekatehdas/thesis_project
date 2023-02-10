package com.example.utilities

import com.google.firebase.database.FirebaseDatabase

object FirebaseDataSaver {
    fun saveData(data: String, table: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("refactor_tests_$table")
        dbRef.push().setValue(data)
    }
}