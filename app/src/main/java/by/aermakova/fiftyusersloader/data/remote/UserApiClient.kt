package by.aermakova.fiftyusersloader.data.remote

import android.util.Log
import by.aermakova.fiftyusersloader.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class UserApiClient {

    fun getUserApiService(): UserGeneratorApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getClient())
            .build()
        return retrofit.create(UserGeneratorApi::class.java)
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(LoggerRequestInterceptor())
            .addInterceptor(LoggerResponseInterceptor())
            .build()
    }

    class LoggerRequestInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val buffer = Buffer()
            request.body?.writeTo(buffer)
            val body: String? = buffer.readUtf8()
            Log.d(
                "Logging request",
                "Headers: ${request.headers}," +
                        " Body: ${if (body.isNullOrBlank()) "null" else body}"
            )
            return chain.proceed(request)
        }
    }

    class LoggerResponseInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            val responseBody = response.body!!
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val contentType = responseBody.contentType()
            val charset: Charset =
                contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            Log.d(
                "Logging response",
                "Headers: ${response.headers}," +
                        " Body: ${buffer.clone().readString(charset)}"
            )
            return response
        }
    }
}