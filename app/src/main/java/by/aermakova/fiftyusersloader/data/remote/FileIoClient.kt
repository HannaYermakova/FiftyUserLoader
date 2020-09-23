package by.aermakova.fiftyusersloader.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val FILE_IO_URL = "https://file.io"

class FileIoClient {

    private lateinit var fileUploadApi: FileUploadApi

    fun getFileUploadApi(): FileUploadApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(FILE_IO_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getClient())
            .build()
        fileUploadApi = retrofit.create(FileUploadApi::class.java)
        return fileUploadApi
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()
                .apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    }
}