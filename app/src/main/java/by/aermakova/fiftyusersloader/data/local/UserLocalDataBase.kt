package by.aermakova.fiftyusersloader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import by.aermakova.fiftyusersloader.data.model.local.User

const val USER_TABLE_NAME = "user_table"
@Database(entities = [User::class], version = 3, exportSchema = false)
abstract class UserLocalDataBase : RoomDatabase() {
    abstract fun usersDao(): UserDao
}