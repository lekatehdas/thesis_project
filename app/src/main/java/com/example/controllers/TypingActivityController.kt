package com.example.controllers

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import java.io.OutputStreamWriter

class TypingActivityController(
    private val context: Context,
    private val dataHolder: DataHolder<Long>,
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

                if (dataHolder.getMinListSize() >= Constants.DESIRED_LENGTH) {
                    saveData()
                    dataHolder.clearAllLists()
                    resetUi()

                } else {
                    val timeStamp = System.nanoTime()

                    if (dataHolder.getListSize(keystroke) < Constants.DESIRED_LENGTH) {
                        val char = s[start + count - 1].code.toLong()

                        dataHolder.addElementToList(keystroke, char)
                    }

                    if (dataHolder.getListSize(time) < Constants.DESIRED_LENGTH)
                        dataHolder.addElementToList(time, timeStamp)

                }

                updateUi()
            }
        })
    }

    private fun saveData() {
        saveListData(keystroke, "Typing")
        saveListData(time, "Time")
    }

    private fun saveListData(listName: String, folderName: String) {
        val sensorData = dataHolder.getListByName(listName) ?: return
        val fileName = "${System.currentTimeMillis()}.csv"
        val contentValues = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/$folderName/")
        }

        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                sensorData.forEach { value ->
                    writer.write("$value\n")
                }
                writer.flush()
            }
        }
    }
}