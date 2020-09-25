package by.aermakova.fiftyusersloader.ui.uploading

import by.aermakova.fiftyusersloader.data.UserInteractor

interface UploadManager {
    fun updateNotification(progress: Int, userId: Int, fileTitle: String)
    fun clearNotification(userId: Int)
    fun getInteractor(): UserInteractor
    fun showErrorMessage(userId: Int)
}