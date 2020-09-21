package by.aermakova.fiftyusersloader.data.model

import by.aermakova.fiftyusersloader.data.model.local.GenderConverter
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.data.model.remote.UserDto

interface UserConverter : Mapper<User, UserDto> {

    override fun UserDto.toLocal(): User {
        return User(
            gender = GenderConverter.convertStringToGender(gender),
            name = "${name.title} ${name.first} ${name.last}",
            email = email,
            login = login.username,
            phone = phone,
            picture = picture.medium
        )
    }
}