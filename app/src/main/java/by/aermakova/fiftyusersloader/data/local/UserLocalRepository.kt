package by.aermakova.fiftyusersloader.data.local

import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.Single
import javax.inject.Inject

class UserLocalRepository @Inject constructor(private var localDB: UserLocalDataBase) {

    fun getAllUsers(): Single<List<User>> {
        return localDB.usersDao().getAllUsers()
    }

    fun deleteAllUsers() {
        localDB.usersDao().deleteAll()
    }

    fun insertAllUsers(users: List<User>) {
        localDB.usersDao().insertAllUsers(users)
    }
}