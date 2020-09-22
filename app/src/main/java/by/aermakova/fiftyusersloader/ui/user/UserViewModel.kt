package by.aermakova.fiftyusersloader.ui.user

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserViewModel @ViewModelInject constructor(private val userInteractor: UserInteractor) :
    ViewModel() {

    init {
        Log.i("UserViewModel", "init")
    }

    private val disposable = CompositeDisposable()

    var currentUserId: Int = -1

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun loadUser() {
        disposable.add(
            userInteractor.getUserById(currentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _user.value = it
                        Log.i("UserViewModel", "$it")
                    },
                    { it.printStackTrace() }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("UserViewModel", "clear")
        disposable.clear()
    }
}