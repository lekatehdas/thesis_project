package com.example.data_generator

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.math.log10

class DecibelDataGenerator(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null

    @RequiresApi(Build.VERSION_CODES.S)
    fun getDecibelLevel(): Double {
        mediaRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile("/dev/null")
            prepare()
            start()
        }

        val decibelLevel = 20.0 * log10(mediaRecorder?.maxAmplitude?.toDouble() ?: 0.0)

        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null

        return decibelLevel
    }
}