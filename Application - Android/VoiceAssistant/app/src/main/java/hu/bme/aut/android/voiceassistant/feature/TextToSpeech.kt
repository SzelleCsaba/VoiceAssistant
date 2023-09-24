package hu.bme.aut.android.voiceassistant.feature

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeech(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale("hu", "HU")
                //textToSpeech?.speak("szép volt fiam!", TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    // TODO speak failed: not bound to TTS engine
    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }
}
