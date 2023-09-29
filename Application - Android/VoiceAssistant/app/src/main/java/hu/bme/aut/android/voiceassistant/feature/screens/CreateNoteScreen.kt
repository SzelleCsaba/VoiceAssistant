package hu.bme.aut.android.voiceassistant.feature.screens

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
import com.google.android.gms.actions.NoteIntents
import hu.bme.aut.android.voiceassistant.R

@Composable
fun CreateNoteScreen(subject: String = "note", text: String, onBackPressed: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = Intent(NoteIntents.ACTION_CREATE_NOTE).apply {
            putExtra(NoteIntents.EXTRA_NAME, subject)
            putExtra(NoteIntents.EXTRA_TEXT, text)
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