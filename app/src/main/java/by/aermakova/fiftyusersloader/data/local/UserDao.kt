package by.aermakova.fiftyusersloader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.Single

@Dao
interface UserDao {

    @Query("SELECT * from user_table")
    fun getAllUsers(): Single<List<User>>

    @Insert
    fun insertAllUsers(users: List<User>)

    @Query("DELETE FROM user_table")
    fun deleteAll()

    @Query("SELECT * from user_table WHERE id = :id ")
    fun getUserById(id: Int): Single<User>
}