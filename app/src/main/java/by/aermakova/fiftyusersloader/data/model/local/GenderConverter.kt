package by.aermakova.fiftyusersloader.data.model.local

import androidx.room.TypeConverter

class GenderConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun convertGenderToString(gender: Gender): String {
            return gender.gender
        }

        @TypeConverter
        @JvmStatic
        fun convertStringToGender(gender: String): Gender {
            return when (gender) {
                "male" -> Gender.MALE
                "female" -> Gender.FEMALE
                else -> Gender.MALE
            }
        }
    }
}