package com.example.pseudorandomgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityMainBinding
import com.example.utilities.PermissionHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionHelper = PermissionHelper(this)
        permissionHelper.checkAndAskPermissions()

        initListeners()
    }

    private fun initListeners() {
        binding.btnTimeButton.setOnClickListener {
            val intent = Intent(this, TimeButtonActivity::class.java)
            startActivity(intent)
        }

        binding.btnTimeTyping.setOnClickListener {
            val intent = Intent(this, TimeTypingActivity::class.java)
            startActivity(intent)
        }

        binding.btnWifi.setOnClickListener {
            val intent = Intent(this, WifiInfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnMovement.setOnClickListener {
            val intent = Intent(this, MovementActivity::class.java)
            startActivity(intent)
        }

        binding.btnSpeech.setOnClickListener {
            val intent = Intent(this, TextToSpeechActivity::class.java)
            startActivity(intent)
        }

        binding.btnTraffic.setOnClickListener {
            val intent = Intent(this, NetworkActivity::class.java)
            startActivity(intent)
        }
    }
}