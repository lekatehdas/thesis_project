import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.utilities.Constants
import com.example.utilities.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KSuspendFunction1

class AudioDataGatherer(
    private val context: Context,
    private val onBufferFilled: KSuspendFunction1<ByteArray, Unit>
) {
    private var isRecording = false
    private var audioRecorder: AudioRecord? = null

    suspend fun start() {
        withContext(Dispatchers.IO) {
            isRecording = true

            val minBufferSize = AudioRecord.getMinBufferSize(
                Constants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                PermissionHelper(context as Activity).checkAndAskPermissions()

            audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                Constants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
            )

            audioRecorder?.startRecording()

            val chunkSize = 2 // number of bytes in each chunk
            val buffer = ByteArray(chunkSize)
            var bufferIndex = 0

            while (isRecording) {
                val readBytes = audioRecorder?.read(buffer, bufferIndex, chunkSize - bufferIndex) ?: 0
                if (readBytes < 0) break

                bufferIndex += readBytes

                // if we have enough bytes for a chunk, store it in the data holder
                if (bufferIndex >= chunkSize) {
                    onBufferFilled(buffer.copyOf())
                    bufferIndex = 0
                }
            }

            audioRecorder?.stop()
            audioRecorder?.release()
            audioRecorder = null
        }
    }

    fun stop() {
        isRecording = false
    }
}
