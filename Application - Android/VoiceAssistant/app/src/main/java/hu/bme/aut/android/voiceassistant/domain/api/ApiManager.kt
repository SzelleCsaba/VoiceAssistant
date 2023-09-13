import hu.bme.aut.android.voiceassistant.domain.api.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ApiFunctions(private val apiService: ApiService) {
    fun interpretText(text: String) {
        val requestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
        val call = apiService.interpretText(requestBody)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Handle the response here
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Process the response body
                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle the failure
            }
        })
    }

    fun interpretVoice(audioPath: String, lang: String) {
        val file = File(audioPath)
        val requestBody = file.asRequestBody("audio/*".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData("audio", file.name, requestBody)
        val langRequestBody = lang.toRequestBody("text/plain".toMediaTypeOrNull())
        val call = apiService.interpretVoice(audioPart, langRequestBody)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Handle the response here
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    // Process the response body
                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle the failure
            }
        })
    }
}
