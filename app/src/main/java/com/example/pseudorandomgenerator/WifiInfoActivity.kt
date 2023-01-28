package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.data_generator.WifiInformationGenerator
import com.example.pseudorandomgenerator.databinding.ActivityWifiBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables

class WifiInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWifiBinding
    private var generatedData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.btnWifiInfo.setOnClickListener {
            wifiInfoData()
        }
    }

    private fun wifiInfoData() {
        val generator = WifiInformationGenerator(this)

        generatedData += generator.getWifiInformationData()

        if (!enoughData()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(
                """
                    Not enough data to generate key.
                    Refresh the wifi search and try again.
                    If problem persists, try in the area with multiple wifi connection points.
                """.trimIndent()
            )

            builder.create().show()
        } else {
            Toast.makeText(this, "Data generation successful", Toast.LENGTH_LONG).show()
            saveData()
            resetData()
        }
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "wifi"
        )
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun enoughData() = generatedData.length >= EnvVariables.DESIRED_LENGTH
}
