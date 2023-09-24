package hu.bme.aut.android.voiceassistant.domain.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.http.Body
import okhttp3.ResponseBody
import java.util.concurrent.TimeUnit

interface ApiService {
    @POST("/text")
    fun interpretText(@Body text: RequestBody): Call<ResponseBody>

    @Multipart
    @POST("/voice")
    fun interpretVoice(@Part audio: MultipartBody.Part, @Part("lang") lang: RequestBody): Call<ResponseBody>
}

object ApiClient {
    private const val BASE_URL = "http://192.168.1.198:8080/" // TODO change to actual url once it is deployed
    // TODO do not forget to remove res/xml/network_security_config once it is not needed + Manifest!

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES) // connect timeout
        .readTimeout(30, TimeUnit.SECONDS) // socket timeout
        .writeTimeout(15, TimeUnit.SECONDS) // write timeout
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }
}