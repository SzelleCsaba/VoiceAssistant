package hu.bme.aut.android.voiceassistant.feature.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import hu.bme.aut.android.voiceassistant.R

// TODO somehow is starts the camera in still image mode :(
@Composable
fun RecordVideoScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current

    val recordVideo = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result and get the recorded video
            val videoUri = result.data?.data
            // Process and use the video as needed
        } else {
            // Handle the failure
        }
    }
    val permissionDenied = stringResource(R.string.permission_denied)

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val recordVideoIntent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
            recordVideo.launch(recordVideoIntent)
            onBackPressed()
        } else {
            Toast.makeText(context, permissionDenied, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val recordVideoIntent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
            recordVideo.launch(recordVideoIntent)
            onBackPressed()
        } else {
            permissionLauncher.launch(permission)
        }

    }

    Button(
        onClick = onBackPressed,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(stringResource(R.string.go_back))
    }
}