package by.aermakova.fiftyusersloader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import by.aermakova.fiftyusersloader.data.model.local.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserLocalDataBase : RoomDatabase() {
    abstract fun usersDao(): UserDao
}