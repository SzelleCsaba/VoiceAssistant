package hu.bme.aut.android.voiceassistant.feature.screens

import android.content.Intent
import android.provider.CalendarContract
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddEventScreen(title: String = "event", date: String, onBackPressed: () -> Unit) {
    val context = LocalContext.current

    val startTimeInMillis = convertDateStringToMillis(date)
    val oneHourInMillis = 60 * 60 * 1000

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTimeInMillis + oneHourInMillis)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
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

fun convertDateStringToMillis(dateString: String): Long {
    val currentDate = Calendar.getInstance()
    var year = currentDate.get(Calendar.YEAR)
    val formattedDateString = "$dateString$year"

    val dateFormat = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
    var date = dateFormat.parse(formattedDateString)
    if (date != null && date.before(currentDate.time)) {
        year++
        val newFormattedDateString = "$dateString$year"
        date = dateFormat.parse(newFormattedDateString)
    }

    return date?.time ?: 0L
}
