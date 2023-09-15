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
fun SetTimerScreen(message: String = "timer", time: String, onBackPressed: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = createSetTimerIntent(message, time)

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    Button(
        onClick = onBackPressed,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Go Back")
    }
}

private fun createSetTimerIntent(message: String, time: String): Intent {
    val timeParts = time.split(":")
    if (timeParts.size == 3) {
        val hour = timeParts[0].toIntOrNull()
        val minute = timeParts[1].toIntOrNull()
        val second = timeParts[2].toIntOrNull()

        if (hour != null && minute != null && second != null) {
            val seconds = second + minute * 60 + hour * 60 * 60

            return Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            }
        }
    }

    // Return a default intent if the time is not valid
    return Intent(AlarmClock.ACTION_SET_TIMER)
}
