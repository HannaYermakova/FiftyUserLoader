package by.aermakova.fiftyusersloader.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "user_table")
data class User(

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,

    @TypeConverters(GenderConverter::class)
    val gender: Gender?,

    val name: String?,
    val email: String?,
    val login: String?,
    val phone: String?,
    val picture: String?
)