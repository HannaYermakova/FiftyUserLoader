package by.aermakova.fiftyusersloader.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "user_table")
@TypeConverters(GenderConverter::class)
data class User(

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,

    var gender: Gender,
    val name: String,
    val email: String,
    val login: String,
    val phone: String,
    val picture: String)