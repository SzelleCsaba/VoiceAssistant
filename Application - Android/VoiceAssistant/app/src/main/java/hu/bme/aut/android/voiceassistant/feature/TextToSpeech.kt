package hu.bme.aut.android.voiceassistant.feature

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeech(context: Context, private val langCode: String, private val countryCode: String) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false


    init {
        textToSpeech = TextToSpeech(context, { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale(langCode, countryCode)
                isInitialized = true
            }
        }, "com.google.android.tts")
    }

    fun speak(text: String) {
        if (isInitialized){
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }
}
