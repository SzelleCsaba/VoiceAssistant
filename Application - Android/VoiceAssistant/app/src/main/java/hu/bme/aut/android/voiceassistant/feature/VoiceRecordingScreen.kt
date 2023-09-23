package hu.bme.aut.android.voiceassistant.feature

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import hu.bme.aut.android.voiceassistant.domain.api.ApiClient
import hu.bme.aut.android.voiceassistant.domain.api.ApiFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun VoiceRecordingScreen() {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }
    val apiService = ApiClient.instance
    val apiFunctions = ApiFunctions(apiService)
    var mediaRecorder: MediaRecorder?



    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    }else {
        MediaRecorder()
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(context.filesDir.path + "/recording.mp4")
                prepare()
                start()
            }
        } else {
                // Handle permission denial
            }
        }

    Button(
        onClick = {

            if (isRecording.value) {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                }
                mediaRecorder = null
                mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                }else {
                    MediaRecorder()
                }
                CoroutineScope(Dispatchers.IO).launch {

                    val response = apiFunctions.interpretVoice(context.filesDir.path + "/recording.mp4", "hu")
                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            Log.i("res", response)
                        }
                    }
                    val recordingFile = File(context.filesDir, "/recording.mp4")
                    if (recordingFile.exists()) {
                        recordingFile.delete()
                    }
                }

            } else {
                val permission = Manifest.permission.RECORD_AUDIO

                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {

                    mediaRecorder?.apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setOutputFile(context.filesDir.path + "recording.mp4")
                        prepare()
                        start()
                    }
                } else {
                    permissionLauncher.launch(permission)
                }
            }
            isRecording.value = !isRecording.value
        }
    ) {
        Text(if (isRecording.value) "Stop Recording" else "Start Recording")
    }
}
// TODO kösd rá, meg rakd át a MainScreenre
