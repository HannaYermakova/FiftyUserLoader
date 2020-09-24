package by.aermakova.fiftyusersloader.ui.userList

sealed class LoadingStatus {
    object Loading : LoadingStatus()
    object Success : LoadingStatus()
    object Error : LoadingStatus()
}