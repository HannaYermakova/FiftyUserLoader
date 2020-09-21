package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.data.model.remote.ListUser
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface UserGeneratorApi {

    @GET("/api/?")
    fun getUsers(@Query("results") results: Int): Single<ListUser>
}