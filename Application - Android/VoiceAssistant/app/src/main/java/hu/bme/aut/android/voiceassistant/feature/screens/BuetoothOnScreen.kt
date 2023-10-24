package hu.bme.aut.android.voiceassistant.feature.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
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

@Composable
fun BluetoothOnScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val bluetoothOn = stringResource(R.string.bluetooth_turned_on)
    val bluetoothError = stringResource(R.string.failed_to_turn_on_bluetooth)

    val enableBluetooth = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Bluetooth is enabled, you can perform Bluetooth operations here
            Toast.makeText(context, bluetoothOn, Toast.LENGTH_SHORT).show()
        } else {
            // Bluetooth enablement failed
            Toast.makeText(context, bluetoothError, Toast.LENGTH_SHORT).show()
        }
    }
    val permissionDenied = stringResource(R.string.permission_denied)

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetooth.launch(enableBluetoothIntent)
            onBackPressed()
        } else {
            Toast.makeText(context, permissionDenied, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.BLUETOOTH
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetooth.launch(enableBluetoothIntent)
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


