package com.example.data_generator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import kotlin.math.log10

//class DecibelDataGenerator(private val context: Context) {
//    private lateinit var mediaRecorder: MediaRecorder
//
//    fun getDecibelLevel(): Double {
//        mediaRecorder = MediaRecorder().apply {
//            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            setOutputFile("/dev/null")
//            prepare()
//            start()
//        }
//
//        val decibelLevel = 20.0 * log10(mediaRecorder.maxAmplitude.toDouble())
//
//        mediaRecorder.stop()
//        mediaRecorder.release()
//        mediaRecorder = MediaRecorder()
//
//        return decibelLevel
//    }
//}

import android.media.AudioFormat
import android.media.AudioRecord
import androidx.core.app.ActivityCompat
import com.example.utilities.PermissionHelper
import kotlin.math.pow

class AudioDataGenerator(private val context: Context) {
    private lateinit var audioRecord: AudioRecord

    fun getDecibelLevel(): Double {
        val audioSource = MediaRecorder.AudioSource.MIC
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionHelper(context as Activity).checkAndAskPermissions()
        }
        audioRecord = AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize)
        audioRecord.startRecording()

        val audioData = ShortArray(bufferSize / 2)
        audioRecord.read(audioData, 0, bufferSize / 2)

        audioRecord.stop()
        audioRecord.release()

        val averageAmplitude = audioData.average()

        return 20.0 * log10(
            averageAmplitude.pow(2.0) / Short.MAX_VALUE.toDouble().pow(2.0)
        )
    }
}