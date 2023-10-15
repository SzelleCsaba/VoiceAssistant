package hu.bme.aut.android.voiceassistant.feature

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.voiceassistant.R

abstract class IntentLauncher(val intentData: String, val onBackPressed: () -> Unit) {
    protected abstract val intent: Intent

    @Composable
    fun launchIntent() {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // App isn't installed, handle accordingly.
                onAppNotInstalled(context)
            }
            onBackPressed()
        }
        Button(
            onClick = onBackPressed,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.go_back))
        }
    }

    protected open fun onAppNotInstalled(context: Context) {
        // Default behavior can be defined here or overridden in subclasses.
    }
}

