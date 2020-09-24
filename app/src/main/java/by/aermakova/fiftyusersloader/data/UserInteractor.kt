package by.aermakova.fiftyusersloader.data

import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.data.model.remote.toLocal
import by.aermakova.fiftyusersloader.data.remote.UserRemoteRepository
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private val localDB: UserLocalRepository,
    private val remoteDB: UserRemoteRepository
) {

    fun getUsers(refresh: Boolean): Single<List<User>> {
        return if (refresh) {
            loadUsersToLocalDB()
        } else {
            loadFromLocalOrRefresh()
        }
    }

    private fun loadFromLocalOrRefresh(): Single<List<User>> {
        return localDB.getAllUsers()
            .flatMap {
                if (it.isEmpty()) {
                    loadUsersToLocalDB()
                } else
                    Single.just(it)
            }
    }

    private fun loadUsersToLocalDB(): Single<List<User>> {
        return remoteDB.getAllUsers()
            .map { it ->
                it.results.map { it.toLocal() }
            }
            .doOnSuccess {
                localDB.deleteAllUsers()
                localDB.insertAllUsers(it)
            }.flatMap {
                localDB.getAllUsers()
            }
    }

    fun getUserById(currentUserId: Int) = localDB.getUserById(currentUserId)

    fun uploadFile(body: MultipartBody.Part) = remoteDB.uploadFile(body)
}