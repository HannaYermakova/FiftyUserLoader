package by.aermakova.fiftyusersloader.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import by.aermakova.fiftyusersloader.data.local.USER_TABLE_NAME

@Entity(tableName = USER_TABLE_NAME)
@TypeConverters(GenderConverter::class)
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gender: Gender,
    val name: String,
    val email: String,
    val login: String,
    val phone: String,
    val picture: String,
    val imageExtension: String
)