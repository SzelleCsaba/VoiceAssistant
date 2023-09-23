package hu.bme.aut.android.voiceassistant.feature

import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SetAlarmScreen(message: String = "alarm", time: String, onBackPressed: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = createSetAlarmIntent(message, time)

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
        onBackPressed()
    }

    Button(
        onClick = onBackPressed,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Go Back")
    }
}

private fun createSetAlarmIntent(message: String, time: String): Intent {
    val timeParts = time.split(":")
    if (timeParts.size == 2) {
        val hour = timeParts[0].toIntOrNull()
        val minute = timeParts[1].toIntOrNull()

        if (hour != null && minute != null) {
            return Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
            }
        }
    }

    // Return a default intent if the time is not valid
    return Intent(AlarmClock.ACTION_SET_ALARM)
}
