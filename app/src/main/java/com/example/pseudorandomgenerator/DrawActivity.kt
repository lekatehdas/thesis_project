package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.pseudorandomgenerator.databinding.ActivityDrawBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.PointWithPressure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter

class DrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrawBinding
    private lateinit var dataHolder: DataHolder<PointWithPressure>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataHolder = DataHolder()
        dataHolder.createList("points")

        binding.progressBarDraw.max = Constants.DESIRED_LENGTH

        binding.drawView.setOnTouchListener { _, event ->
            val pointWithPressure = PointWithPressure(event.x, event.y, event.pressure)
            addPoint(pointWithPressure)
            true
        }
    }

    private fun addPoint(pointWithPressure: PointWithPressure) {
        dataHolder.addElementToList("points", pointWithPressure)

        if (dataHolder.getListSize("points") >= Constants.DESIRED_LENGTH) {
            saveData()
            dataHolder.clearList("points")

            CoroutineScope(Dispatchers.Main).launch {
                resetUiElements()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            updateProgressInUi()
        }
    }

    private fun saveData() {
        try {
            val fileName = "${System.currentTimeMillis()}.csv"
            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                put(MediaStore.Files.FileColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Draw/")
            }

            val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            uri?.let {
                contentResolver.openOutputStream(uri).use { outputStream ->
                    val writer = OutputStreamWriter(outputStream)
                    val points = dataHolder.getListByName("points")
                    points.forEach { point ->
                        writer.write("${point.x},${point.y},${point.pressure}\n")
                    }
                    writer.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    @SuppressLint("SetTextI18n")
    private suspend fun resetUiElements() {
        binding.progressBarDraw.progress = 0
        binding.txtProgressBarSDrawPercent.text = "0%"
    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateProgressInUi() {
        binding.progressBarDraw.progress = dataHolder.getListSize("points")

        val percentage = (
                binding.progressBarDraw.progress.toFloat() /
                        binding.progressBarDraw.max.toFloat()) * 100
        binding.txtProgressBarSDrawPercent.text = "${percentage.toInt()}%"
    }
}