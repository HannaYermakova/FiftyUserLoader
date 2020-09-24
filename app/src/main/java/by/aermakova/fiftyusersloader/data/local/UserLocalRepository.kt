package by.aermakova.fiftyusersloader.data.local

import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.Single
import javax.inject.Inject

class UserLocalRepository @Inject constructor(private val userDao: UserDao) {

    fun getAllUsers(): Single<List<User>> {
        return userDao.getAllUsers()
    }

    fun deleteAllUsers() {
        userDao.deleteAll()
    }

    fun insertAllUsers(users: List<User>) {
        userDao.insertAllUsers(users)
    }

    fun getUserById(id: Int) : Single<User>{
        return userDao.getUserById(id)
    }
}