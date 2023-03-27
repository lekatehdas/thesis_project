import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.runBlocking
import java.io.OutputStreamWriter

class AudioActivityController(
    private val context: Context,
    private val dataHolder: DataHolder<ByteArray>,
    private val audio: String,
    private val updateProgress: suspend () -> Unit,
    private val resetUi: suspend () -> Unit
) {
    private val gatherer = AudioDataGatherer(context, ::onData)

    suspend fun start() {
        gatherer.start()
    }

    fun stop() {
        gatherer.stop()
    }

    private suspend fun onData(buffer: ByteArray) {
        dataHolder.addElementToList(audio, buffer)

        if (dataHolder.getListSize(audio) >= Constants.DESIRED_LENGTH) {
            saveData()
            dataHolder.clearList(audio)

            (context as Activity).runOnUiThread { runBlocking { resetUi() } }
        }

        (context as Activity).runOnUiThread { runBlocking { updateProgress() } }
    }

    private fun saveData() {
        try {
            val fileName = "${System.currentTimeMillis()}.csv"
            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                put(MediaStore.Files.FileColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Audio/")
            }

            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    val writer = OutputStreamWriter(outputStream)
                    val audioData = dataHolder.getListByName(audio)
                    audioData.forEach { buffer ->
                        val binaryStrings = buffer.map { String.format("`%8s", Integer.toBinaryString(it.toInt() and 0xff)).replace(' ', '0') }
                        val byteArrayString = binaryStrings.joinToString(",")
                        writer.write("$byteArrayString\n")
                    }
                    writer.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}