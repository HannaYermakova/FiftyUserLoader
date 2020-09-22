package by.aermakova.fiftyusersloader.data.remote

import android.util.Log
import by.aermakova.fiftyusersloader.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)*/

        val inters = object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                Log.i("Logger", "Header: ${chain.request().headers}, Body: ${chain.request().body} ")
                return chain.proceed(request)
            }
        }

       return OkHttpClient.Builder().addInterceptor(inters).build()
    }
}