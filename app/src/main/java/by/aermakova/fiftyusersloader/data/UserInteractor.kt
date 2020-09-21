package by.aermakova.fiftyusersloader.data

import android.app.Application
import by.aermakova.fiftyusersloader.data.local.UserLocalRepository
import by.aermakova.fiftyusersloader.data.model.UserConverter
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.data.remote.UserRemoteRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UserInteractor(application: Application) : UserConverter {

    val localDB = UserLocalRepository(application)
    val remoteDB = UserRemoteRepository

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