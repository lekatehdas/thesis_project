package com.example.controllers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_processors.ListDataProcessor
import com.example.data_processors.LongDataProcessor
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver
import java.math.BigInteger

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

                if (dataHolder.getSizeOfSmallestArray() >= Constants.DESIRED_LENGTH) {
                    saveData()
                    dataHolder.resetData()
                    resetUi()

                } else {
                    val timeLong = System.nanoTime()
                    val timeByte = LongDataProcessor.getLeastSignificantByte(timeLong)

                    if (dataHolder.getSizeOfAnArray(keystroke) < Constants.DESIRED_LENGTH) {
                        val char = s[start + count - 1].toString().toByteArray()
                        val scrambleChar = scrambleChar(char, timeByte)

                        dataHolder.concatArray(keystroke, scrambleChar)
                    }

                    if (dataHolder.getSizeOfAnArray(time) < Constants.DESIRED_LENGTH)
                        dataHolder.concatArray(time, timeByte)

                }

                updateUi()
            }
        })
    }

    private fun saveData() {
        val lists = dataHolder.getAllArrays()

        val result = ListDataProcessor.combineByteArraysByXOR(lists)
        val string = ByteArrayToBinaryStringConverter.convert(result)

        FirebaseDataSaver.saveData(
            data = string,
            table = "typing"
        )

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(keystroke)),
            table = "typing_keystroke_alone"
        )

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(time)),
            table = "typing_time_alone"
        )
    }

    private fun scrambleChar(char: ByteArray, time: ByteArray): ByteArray {
        val result = (BigInteger(time) + BigInteger(char)) % Constants.PRIME_FOR_MOD.toBigInteger()
        return result.toByteArray()
    }
}