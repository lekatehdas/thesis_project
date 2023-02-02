package com.example.utilities

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object DataSaver {
    fun saveData(data: String, table: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference(table)
        dbRef.push().setValue(data)
    }

    fun appendDataToKey(data: String, key: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(key)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                myRef.setValue("$value$data")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}