package by.aermakova.fiftyusersloader.data.model.local

import androidx.room.TypeConverter

object GenderConverter {

    @TypeConverter
    @JvmStatic
    fun convertGenderToString(gender: Gender): String {
        return gender.gender
    }

    @TypeConverter
    @JvmStatic
    fun convertStringToGender(gender: String): Gender {
        return when (gender) {
            Gender.FEMALE.gender -> Gender.FEMALE
            else -> Gender.MALE
        }
    }
}