//package com.example.pseudorandomgenerator
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.example.converters.ByteArrayToBinaryStringConverter
//import com.example.pseudorandomgenerator.databinding.ActivitySpeechToTextBinding
//import com.example.data_gatherers.SpeechDataGatherer
//import com.example.data_processors.ListDataProcessor
//import com.example.data_processors.ByteArrayProcessor.combineAndHalveByteArray
//import com.example.utilities.*
//
//class SpeechToTextActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySpeechToTextBinding
//    private lateinit var dataHolder: DataHolder
//
//    private lateinit var gatherer: SpeechDataGatherer
//
//    private val speech = "speechData"
//    private val audio = "audioData"
//    private val sources = listOf(
//        speech,
//        audio
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySpeechToTextBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        gatherer = SpeechDataGatherer(this)
//
//        binding.progressBarSpeech.max = Constants.DESIRED_LENGTH
//
//        initDataHolder()
//        initListener()
//    }
//
//    private fun initListener() {
//        binding.btnStartSpeech.setOnClickListener {
//            gatherer.startVoiceRecognition()
//        }
//    }
//
//    private fun initDataHolder() {
//        dataHolder = DataHolder()
//        for (name in sources)
//            dataHolder.initializeArray(name)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val listOfData: List<ByteArray> =  gatherer.onActivityResult(requestCode, resultCode, data)
//
//        if (!enoughDataFor(speech)) {
//            addDataToHolder(listOfData[0], speech)
//        }
//
//        if (!enoughDataFor(audio)) {
//            addDataToHolder(listOfData[1], audio)
//        }
//
//        updateUiElements()
//
//        if (enoughData()) {
//            saveData()
//            resetData()
//            resetUiElements()
//        }
//
//        gatherer.startVoiceRecognition()
//    }
//
//    private fun addDataToHolder(array: ByteArray, dataArrayName: String) {
//        var data = array
//
//        do { data = combineAndHalveByteArray(data) }
//        while (data.size > Constants.DESIRED_LENGTH * 2)
//
//        dataHolder.concatArray(dataArrayName, data)
//    }
//
//    private fun saveData() {
//        val lists = dataHolder.getAllArrays()
//
//        val xor = ListDataProcessor.combineByteArraysByXOR(lists)
//        val result = ByteArrayToBinaryStringConverter.convert(xor)
//
//        FirebaseDataSaver.saveData(
//            data = result,
//            table = "speech"
//        )
//
//        FirebaseDataSaver.saveData(
//            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(audio)),
//            table = "speech_audio_alone"
//        )
//
//        FirebaseDataSaver.saveData(
//            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(speech)),
//            table = "speech_text_alone"
//        )
//    }
//
//    private fun enoughDataFor(name: String) = dataHolder.getSizeOfAnArray(name) >= Constants.DESIRED_LENGTH
//
//    private fun resetData() {
//        dataHolder.resetData()
//    }
//
//    private fun resetUiElements() {
//        binding.progressBarSpeech.progress = 0
//        binding.txtProgressBarSpeechPercent.text = "0%"
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun updateUiElements() {
//        binding.progressBarSpeech.progress = dataHolder.getSizeOfSmallestArray()
//
//        val percentage =
//            (binding.progressBarSpeech.progress.toFloat() / binding.progressBarSpeech.max.toFloat()) * 100
//        binding.txtProgressBarSpeechPercent.text = "${percentage.toInt()}%"
//    }
//
//    private fun enoughData(): Boolean {
//        return dataHolder.getSizeOfSmallestArray() >= Constants.DESIRED_LENGTH
//    }
//}