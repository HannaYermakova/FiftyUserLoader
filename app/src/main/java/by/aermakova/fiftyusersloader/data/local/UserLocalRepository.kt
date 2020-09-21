package by.aermakova.fiftyusersloader.data.local

import android.app.Application
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.Single

class UserLocalRepository(application: Application) {

    val userLocalDB = UserLocalDataBase.getDatabase(application.applicationContext).usersDao()

    fun getAllUsers(): Single<List<User>> {
        return userLocalDB.getAllUsers()
    }

    fun deleteAll() {
        userLocalDB.deleteAll()
    }

    fun insertAll(users: List<User>) {
        userLocalDB.insertAllUsers(users)
    }
}