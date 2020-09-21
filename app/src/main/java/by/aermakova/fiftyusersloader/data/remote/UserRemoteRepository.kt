package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.data.model.remote.ListUser
import io.reactivex.Single

const val INITIAL_NUMBER = 50

object UserRemoteRepository {

    fun getAllUsers(): Single<ListUser> {
        return UserApiClient.getUserApiService().getUsers(INITIAL_NUMBER)
    }
}