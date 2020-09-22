package by.aermakova.fiftyusersloader.data.model.remote

import by.aermakova.fiftyusersloader.data.model.local.GenderConverter
import by.aermakova.fiftyusersloader.data.model.local.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("gender") val gender : String,
    @SerializedName("name") val name : Name,
    @SerializedName("email") val email : String,
    @SerializedName("login") val login : Login,
    @SerializedName("phone") val phone : String,
    @SerializedName("picture") val picture : Picture
)

fun UserDto.toLocal(): User {
    return User(
        gender = GenderConverter.convertStringToGender(gender),
        name = "${name.title} ${name.first} ${name.last}",
        email = email,
        login = login.username,
        phone = phone,
        picture = picture.medium
    )
}