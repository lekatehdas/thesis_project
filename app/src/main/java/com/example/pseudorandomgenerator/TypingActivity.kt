package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.pseudorandomgenerator.databinding.ActivityTypeingBinding
import com.example.utilities.*
import java.security.SecureRandom
import kotlin.random.Random

class TypingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypeingBinding

    private var keystrokeData = ByteArray(0)
    private var timeData = ByteArray(0)
    private var alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private var secRandom: SecureRandom = SecureRandom()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarTimeTyping.max = EnvVariables.DESIRED_LENGTH
        initListeners()
        }


    private fun runTest(){
        for (i in 1..10000){
            for (i in 1..64){
                val randChar : String = alphabet[secRandom.nextInt(alphabet.size)].toString()
                Log.d("Letter", randChar)
                binding.editTxtTimeTyping.setText(randChar)
                Thread.sleep(secRandom.nextInt(40)+40.toLong())
            }
            if (i %10 ==0) Log.d("ITERATION", i.toString())
        }
    }

    private fun initListeners() {
        binding.btnTypingTest.setOnClickListener()
        {
            Log.d("AUTO_DATA", "AUTO_DATA run start")
            runTest()
            Log.d("AUTO_DATA", "AUTO_DATA run end")
        }
        binding.editTxtTimeTyping.addTextChangedListener(/* watcher = */ object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.isEmpty()) {
                    return
                }

                if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    resetUiElements()

                } else {
                    if (keystrokeData.size < EnvVariables.DESIRED_LENGTH)
                        keystrokeData += s[start + count - 1].toString().toByteArray()

                    if (timeData.size < EnvVariables.DESIRED_LENGTH)
                        timeData += LeastSignificantBits.getSystemNanoTime()

                    updateUiElements()
                }
            }
        })
    }

    private fun saveData() {
        val lists = listOf(
            timeData,
            keystrokeData
        )

        val result = ByteArrayListXOR.xor(lists)
        val string = ByteArrayToBinaryStringConverter.convert(result)

        DataSaver.saveData(
            data = string,
            table = "typing"
        )

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(keystrokeData),
            table = "keystroke_alone"
        )
    }

    @SuppressLint("SetTextI18n")
    fun updateUiElements() {
        binding.progressBarTimeTyping.progress = smallestArraySize()

        val percentage =
            (binding.progressBarTimeTyping.progress.toFloat() / binding.progressBarTimeTyping.max.toFloat()) * 100
        binding.txtTimeTypingBarPercent.text = "${percentage.toInt()}%"
    }

    private fun resetData() {
        keystrokeData = ByteArray(0)
        timeData = ByteArray(0)
    }

    private fun resetUiElements() {
        binding.editTxtTimeTyping.text.clear()
        binding.editTxtTimeTyping.clearFocus()
        binding.progressBarTimeTyping.progress = 0
        binding.txtTimeTypingBarPercent.text = "0%"
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            keystrokeData,
            timeData
        )
        return arrays.minBy { it.size }.size ?: 0
    }
}