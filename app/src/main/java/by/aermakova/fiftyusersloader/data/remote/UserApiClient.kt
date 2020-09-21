package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.BuildConfig
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
            .build()
        userApiService = retrofit.create(UserGeneratorApi::class.java)
        return userApiService
    }
}