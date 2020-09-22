package by.aermakova.fiftyusersloader.data.remote

import android.util.Log
import by.aermakova.fiftyusersloader.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import okio.GzipSink
import okio.Okio
import okio.buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class UserApiClient {

    private lateinit var userApiService: UserGeneratorApi

    fun getUserApiService(): UserGeneratorApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getClient())
            .build()
        userApiService = retrofit.create(UserGeneratorApi::class.java)
        return userApiService
    }

    private fun getClient(): OkHttpClient {
/*        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)*/
        return OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()
    }

    class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.i(
                "Logging request",
                "Headers: ${request.headers}, Body: ${request.body.toString()}"
            )
            val response = chain.proceed(request)
            Log.i(
                "Logging response",
                "Headers: ${response.headers}, Body: ${response.body.toString()}"
            )
            return response
        }
    }
}