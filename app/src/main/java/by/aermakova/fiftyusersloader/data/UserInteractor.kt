package by.aermakova.fiftyusersloader.data

import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.model.UserConverter
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.data.remote.UserRemoteRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private var localDB: UserLocalRepository,
    private var remoteDB: UserRemoteRepository
) : UserConverter {

    fun getUsers(update: Boolean): Single<List<User>> {
        return if (!update) {
            updateUsersInLocalDB()
        } else {
            isLocalDBEmpty()
        }
    }

    private fun isLocalDBEmpty(): Single<List<User>> {
        return localDB.getAllUsers()
            .subscribeOn(Schedulers.io())
            .flatMap {
                Single.create<List<User>> { emitter ->
                    if (it.isEmpty()) {
                        updateUsersInLocalDB()
                    } else {
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(it)
                        }
                    }
                }
            }
    }

    private fun updateUsersInLocalDB(): Single<List<User>> {
        return remoteDB.getAllUsers()
            .subscribeOn(Schedulers.io())
            .flatMap { it ->
                Single.create<List<User>> { emitter ->
                    localDB.deleteAll()
                    val list = it.results.map { it.toLocal() }
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(list)
                    }
                }
            }
    }
}