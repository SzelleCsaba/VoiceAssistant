package hu.bme.aut.android.voiceassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import hu.bme.aut.android.voiceassistant.ui.theme.VoiceAssistantTheme

import hu.bme.aut.android.voiceassistant.navigation.NavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceAssistantTheme {
                NavGraph()
            }
        }
    }
}