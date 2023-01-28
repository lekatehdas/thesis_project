package com.example.utilities

import com.google.firebase.database.FirebaseDatabase

object DataSaver {
    fun saveData(data: String, table: String) {
        val finalString = StringTruncator.truncate(data, EnvVariables.DESIRED_LENGTH)
        val dbRef = FirebaseDatabase.getInstance().getReference(table)
        dbRef.push().setValue(finalString)
    }
}