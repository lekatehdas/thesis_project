import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.runBlocking

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
        val audioData = dataHolder.getListByName(audio) ?: return
        val fileName = "${System.currentTimeMillis()}.txt"
        val contentValues = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
            put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Audio/"
            )
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )
        uri?.let {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                audioData.forEach { buffer ->
                    if (buffer.size == 8) {
                        val binaryString = StringBuilder()
                        for (i in 0 until buffer.size) {
                            binaryString.append(String.format("%8s", Integer.toBinaryString(buffer[i].toInt() and 0xFF)).replace(' ', '0'))
                        }
                        outputStream.write("$binaryString\n".toByteArray())
                    }
                }
            }
        }
    }
}