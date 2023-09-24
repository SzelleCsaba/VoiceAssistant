package hu.bme.aut.android.voiceassistant.domain.api

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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

    suspend fun interpretVoice(audioPath: String, lang: String): String? {
        val file = File(audioPath)

        // Check if the audio file is shorter than 1 second
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(audioPath)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration
        mediaPlayer.release()

        if (duration < 1000) {
            return null
        }


        val requestBody = file.asRequestBody("audio/*".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData("audio", file.name, requestBody)
        val langRequestBody = lang.toRequestBody("text/plain".toMediaTypeOrNull())
        val call = apiService.interpretVoice(audioPart, langRequestBody)

        return withContext(Dispatchers.IO) {
            try {
                val response = call.execute()

                if (response.isSuccessful) {
                    Log.i("api", "sikeres")
                    response.body()?.string()
                } else {
                    Log.i("api", "nem sikeres")
                    null
                }
            } catch (e: IOException) {
                Log.i("api", e.toString())
                null

            }
        }
    }
}

