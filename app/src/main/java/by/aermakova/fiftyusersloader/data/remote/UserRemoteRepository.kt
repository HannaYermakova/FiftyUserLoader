package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.data.model.remote.ListUser
import io.reactivex.Single
import javax.inject.Inject

const val INITIAL_NUMBER = 50

class UserRemoteRepository @Inject constructor(private var userGeneratorApi: UserGeneratorApi) {

    fun getAllUsers(): Single<ListUser> {
        return userGeneratorApi.getUsers(INITIAL_NUMBER)
    }
}