package com.example.controllers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.data_processors.LsbExtractor
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver

class TypingActivityController(
    private val dataHolder: DataHolder,
    private val sources: List<String>,
    private val updateUi: () -> Unit,
    private val resetUi: () -> Unit,
    private val binding: EditText
) {

    private val keystroke = sources[0]
    private val time = sources[1]
    fun start() {
        binding.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.isEmpty()) {
                    return
                }

                if (dataHolder.getSizeOfSmallestList() >= Constants.DESIRED_LENGTH) {
                    saveData()
                    dataHolder.resetData()
                    resetUi()

                } else {
                    val timeLong = System.currentTimeMillis()
                    val timeBit = LsbExtractor.long(timeLong)


                    val char = s[start + count - 1]
                    val charBit = LsbExtractor.char(char)

                    dataHolder.addToList(keystroke, charBit)
                    dataHolder.addToList(time, timeBit)
                }

                updateUi()
            }
        })
    }

    private fun saveData() {
        FirebaseDataSaver.saveData(
            data = dataHolder.getList(keystroke),
            table = "keystroke"
        )

        FirebaseDataSaver.saveData(
            data = dataHolder.getList(time),
            table = "time"
        )
    }
}