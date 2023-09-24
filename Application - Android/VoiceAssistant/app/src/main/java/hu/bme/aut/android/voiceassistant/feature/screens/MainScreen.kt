package hu.bme.aut.android.voiceassistant.feature.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import hu.bme.aut.android.voiceassistant.domain.api.ApiFunctions
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import hu.bme.aut.android.voiceassistant.domain.api.ApiClient
import hu.bme.aut.android.voiceassistant.feature.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onSendClick: (String) -> Unit) {
    var routeText by remember { mutableStateOf("") }
    val apiService = ApiClient.instance
    val apiFunctions = ApiFunctions(apiService)
    val context = LocalContext.current
    val isProcessing = remember { mutableStateOf(false) }


    val isRecording = remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder?



    // init
    mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }

        if (grantedPermissions.contains(Manifest.permission.RECORD_AUDIO) && grantedPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(context.filesDir.path + "/recording.mp4")
                prepare()
                start()
            }
        } else {
            // Handle permission denial for RECORD_AUDIO and/or CAMERA
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                Modifier.size(width = 360.dp, height = 200.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TextField(
                        value = routeText,
                        onValueChange = { routeText = it },
                        label = { Text("Enter your command") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            isProcessing.value = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = apiFunctions.interpretText(routeText)
                                withContext(Dispatchers.Main) {
                                    isProcessing.value = false
                                    handleRoute(response, context, onSendClick)
                                }
                            }
                        },
                    ) {
                        Text("Run")
                    }
                }
            }
            Spacer(modifier = Modifier.height(200.dp))

            //VoiceRecordingScreen()
            Button(
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 15.dp,
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = {

                    // stop recording
                    if (isRecording.value) {
                        mediaRecorder?.apply {
                            stop()
                            reset()
                            release()
                        }
                        mediaRecorder = null
                        // re init
                        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            MediaRecorder(context)
                        } else {
                            MediaRecorder()
                        }

                        isProcessing.value = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = apiFunctions.interpretVoice(
                                context.filesDir.path + "/recording.mp4",
                                "hu"
                            )
                            withContext(Dispatchers.Main) {
                                isProcessing.value = false
                                handleRoute(response, context, onSendClick)
                            }
                            val recordingFile = File(context.filesDir, "recording.mp4")
                            if (recordingFile.exists()) {
                                recordingFile.delete()
                            }
                        }
                    } else { // start recording
                        val permissions = arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )

                        val deniedPermissions = permissions.filter {
                            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                        }

                        if (deniedPermissions.isEmpty()) {
                            mediaRecorder?.apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                setOutputFile(context.filesDir.path + "/recording.mp4")
                                prepare()
                                start()
                            }
                        } else {
                            permissionLauncher.launch(permissions)
                        }
                    }
                    isRecording.value = !isRecording.value


                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRecording.value) "Stop"  else "Start",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Recording",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        if (isProcessing.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    } //TODO Toast helyett snackbar üzenetek
}

private fun handleRoute(response: String?, context: Context, onSendClick: (String) -> Unit) {
    if (response != null) {
        // Do something with the response
        Log.i("res", response)

        // Safety net
        if (response.length > 10) {
            val jsonObject = JSONObject(response)
            val name = jsonObject.getString("name")

            val argumentsString = jsonObject.getString("arguments")
            val argumentsJsonObject = JSONObject(argumentsString)
            val arguments = mutableMapOf<String, String>()
            val keys = argumentsJsonObject.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val value = argumentsJsonObject.getString(key)
                arguments[key] = value
            }

            if (arguments.isEmpty()) {
                onSendClick(name) // Call the onSendClick function with the name parameter
            } else {
                val concatenatedArguments =
                    arguments.map { "${it.key}=${it.value}" }
                        .joinToString("&")
                onSendClick("$name?$concatenatedArguments") // Call the onSendClick function with the concatenated arguments
            }
        } else {
            Toast.makeText(
                context,
                "There is no function to do it!",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(
            context,
            "Something went wrong",
            Toast.LENGTH_SHORT
        ).show()
    }
}

