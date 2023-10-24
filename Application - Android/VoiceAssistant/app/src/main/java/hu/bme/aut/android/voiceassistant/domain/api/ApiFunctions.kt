package hu.bme.aut.android.voiceassistant.domain.api

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException


class ApiFunctions(private val apiService: ApiService) {
    suspend fun interpretText(text: String): String? {
        val jsonObject = JSONObject()
        jsonObject.put("text", text)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val call = apiService.interpretText(requestBody)

        return withContext(Dispatchers.IO) {
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()?.string()
                } else {
                    null

                }
            } catch (e: IOException) {
                null
            }
        }
    }

    suspend fun interpretVoice(context: Context, audioPath: String, lang: String): String? {
        // Check if the audio file is shorter than 1 second
        val mediaPlayer = MediaPlayer()

        try {

            mediaPlayer.setDataSource(audioPath) // Set the data source
            mediaPlayer.prepare() // Prepare the MediaPlayer
            val duration = mediaPlayer.duration
            Log.i("duration", duration.toString())
            if (duration < 1000) {
                return null
            }

            val audioFile = File(audioPath)
            if (!audioFile.exists()) {
                Log.i("ApiFunctions", "File does not exist")
                return null
            }
            val requestBody = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)
            val langRequestBody = lang.toRequestBody("text/plain".toMediaTypeOrNull())
            val call = apiService.interpretVoice(audioPart, langRequestBody)

            return withContext(Dispatchers.IO) {
                try {
                    val response = call.execute()

                    if (response.isSuccessful) {
                        response.body()?.string()
                    } else {
                        Log.i("ApiFunctions", "API call was not successful. Response message: ${response?.message()}")
                        null
                    }
                } catch (e: IOException) {
                    Log.e("api", e.toString())
                    null
                }
            }
        } finally {
            mediaPlayer.release()
        }
    }
}

