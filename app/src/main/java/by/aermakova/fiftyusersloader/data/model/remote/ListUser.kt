package by.aermakova.fiftyusersloader.data.model.remote

import com.google.gson.annotations.SerializedName

data class ListUser(
    @SerializedName("results") val results: List<UserDto>
)