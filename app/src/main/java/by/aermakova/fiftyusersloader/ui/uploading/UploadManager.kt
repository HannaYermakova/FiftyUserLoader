package by.aermakova.fiftyusersloader.ui.uploading

import by.aermakova.fiftyusersloader.data.UserInteractor

interface UploadManager {
    fun updateNotification(progress: Int, userId: Int)
    fun clearNotification(userId: Int)
    fun getInteractor(): UserInteractor
}