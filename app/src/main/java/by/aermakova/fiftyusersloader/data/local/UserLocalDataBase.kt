package by.aermakova.fiftyusersloader.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.aermakova.fiftyusersloader.data.model.local.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserLocalDataBase : RoomDatabase() {

    abstract fun usersDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserLocalDataBase? = null

        fun getDatabase(context: Context): UserLocalDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserLocalDataBase::class.java,
                    "user_table"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}