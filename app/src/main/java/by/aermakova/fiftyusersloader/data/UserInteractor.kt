package by.aermakova.fiftyusersloader.data

import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.model.UserConverter
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.data.remote.UserRemoteRepository
import io.reactivex.Single
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private var localDB: UserLocalRepository,
    private var remoteDB: UserRemoteRepository
) : UserConverter {

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
                    Single.just<List<User>>(it)
            }
    }

    private fun loadUsersToLocalDB(): Single<List<User>> {
        return remoteDB.getAllUsers()
            .flatMap { it ->
                Single.create<List<User>> { emitter ->
                    localDB.deleteAllUsers()
                    val list = it.results.map { it.toLocal() }
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(list)
                        localDB.insertAllUsers(list)
                    }
                }
            }
    }
}