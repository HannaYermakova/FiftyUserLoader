package by.aermakova.fiftyusersloader.data.remote

import by.aermakova.fiftyusersloader.data.model.remote.ListUser
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

const val INITIAL_NUMBER = 50

class UserRemoteRepository @Inject constructor(
    private val userGeneratorApi: UserGeneratorApi,
    private val fileUploadApi: FileUploadApi
) {

    fun getAllUsers(): Single<ListUser> {
        return userGeneratorApi.getUsers(INITIAL_NUMBER)
    }

    fun uploadFile(file: MultipartBody.Part): Single<ResponseBody> {
        return fileUploadApi.uploadFile(file)
    }
}