package by.aermakova.fiftyusersloader.data.model.remote

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("gender") val gender : String,
    @SerializedName("name") val name : Name,
    @SerializedName("email") val email : String,
    @SerializedName("login") val login : Login,
    @SerializedName("phone") val phone : String,
    @SerializedName("picture") val picture : Picture
)