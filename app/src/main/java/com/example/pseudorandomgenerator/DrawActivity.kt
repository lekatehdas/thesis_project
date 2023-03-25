package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pseudorandomgenerator.databinding.ActivityDrawBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class PointWithPressure(val x: Float, val y: Float, val pressure: Float)

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
            saveData(dataHolder.getListByName("points"))
            dataHolder.clearList("points")

            CoroutineScope(Dispatchers.Main).launch {
                resetUiElements()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            updateProgressInUi()
        }
    }

    private fun saveData(points: List<PointWithPressure>?) {
        // code for saving the points with pressure goes here
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
