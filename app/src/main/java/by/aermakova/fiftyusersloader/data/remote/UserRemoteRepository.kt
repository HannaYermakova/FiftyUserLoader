package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.data.model.remote.ListUser
import io.reactivex.Single
import javax.inject.Inject

const val INITIAL_NUMBER = 50

class UserRemoteRepository @Inject constructor(private var userApiClient: UserApiClient) {

    fun getAllUsers(): Single<ListUser> {
        return userApiClient.getUserApiService().getUsers(INITIAL_NUMBER)
    }
}