package hu.bme.aut.android.voiceassistant.feature.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.android.voiceassistant.feature.TextToSpeech

@Composable
fun ShowAnswerScreen(text: String, onBackPressed: () -> Unit, tts: TextToSpeech) {
    LaunchedEffect(Unit) {
        tts.speak(text)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onBackPressed() }
            .padding(15.dp)
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 35.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
