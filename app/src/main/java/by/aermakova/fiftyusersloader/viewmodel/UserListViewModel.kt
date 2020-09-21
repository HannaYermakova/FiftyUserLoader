package by.aermakova.fiftyusersloader.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.disposables.CompositeDisposable

class UserListViewModel @ViewModelInject constructor(private val userInteractor: UserInteractor) :
    ViewModel() {

    init {
        loadOrUpdateUsers()
    }

    lateinit var userList: List<User>

    private val _disposable = CompositeDisposable()
    val disposable: CompositeDisposable
        get() = _disposable

    private fun loadUsers() =
        userInteractor.getUsers(false)

    private fun updateUsers() =
        userInteractor.getUsers(true)

    private fun loadOrUpdateUsers() {
        _disposable.add(
            loadUsers()
                .subscribe(
                    {
                        userList = it
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }


    fun clearDisposable() {
        _disposable.clear()
    }
}