package com.example.data_generator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import java.io.InputStream


class SpeechDataGenerator(private val context: Context) {
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
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): List<ByteArray> {
        if (!activitySuccessful(requestCode, resultCode)) {
            return listOf(ByteArray(0), ByteArray(0))
        }

        if (data == null) return listOf(ByteArray(0), ByteArray(0))


        val stringResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?: return listOf(ByteArray(0), ByteArray(0))
        val stringByteArray = stringResult.joinToString(" ").toByteArray()

        val audioUri = data.data
        val audioData: InputStream = audioUri?.let { context.contentResolver.openInputStream(it) }
            ?: return listOf(ByteArray(0), ByteArray(0))

        val audioByteArray = audioData.readBytes()
        audioData.close()

        return listOf(stringByteArray, audioByteArray)
    }

    private fun activitySuccessful(requestCode: Int, resultCode: Int) =
        requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK
}