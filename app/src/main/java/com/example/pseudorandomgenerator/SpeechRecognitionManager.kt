package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import java.io.InputStream
import java.math.BigInteger
import java.nio.charset.Charset
import kotlin.experimental.xor


class SpeechRecognitionManager(private val context: Context) {
    private val REQUEST_CODE = 100

    fun startVoiceRecognition() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);

        (context as Activity).startActivityForResult(intent, REQUEST_CODE)
    }

    @SuppressLint("Recycle")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): String {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val resultBytes = results?.joinToString(" ")?.toByteArray()

            val audioUri = data?.data
            val filestream: InputStream? = audioUri?.let {
                context.contentResolver.openInputStream(it)
            }

            val audioResult = filestream?.readBytes()
            filestream?.close()

            val audioReversed = audioResult?.reversedArray()
            val audioSliced = audioReversed?.sliceArray(0 until resultBytes?.size!!)

            val xorArray = xorByteArrays(resultBytes!!, audioSliced!!)

            return byteArrayToHexString(xorArray)
        }

        return ""
    }

    private fun byteArrayToHexString(bytes: ByteArray): String {
        return bytes.joinToString(separator = "") {
            it.toString(16).padStart(2, '0')
        }
    }

    private fun xorByteArrays(a: ByteArray, b: ByteArray): ByteArray {
        return a.zip(b) { x, y -> (x xor y) }.toByteArray()
    }
}