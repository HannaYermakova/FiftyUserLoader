package by.aermakova.fiftyusersloader.ui.userList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class UserListViewModel @ViewModelInject constructor(private val userInteractor: UserInteractor) :
    ViewModel() {

    private val _userList = BehaviorSubject.create<List<User>>()
    val userList: Observable<List<User>>
        get() = _userList.hide()

    private val _refreshing = BehaviorSubject.create<Boolean>()
    val refreshing: Observable<Boolean>
        get() = _refreshing.hide()

    private val _disposable = CompositeDisposable()
    val disposable: CompositeDisposable
        get() = _disposable

    init {
        loadOrRefreshUsers(refresh = false)
    }

    val load = { loadOrRefreshUsers(true) }

    private fun loadOrRefreshUsers(refresh: Boolean) {
        _disposable.add(
            userInteractor.getUsers(refresh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _userList.onNext(it)
                        if (refresh) {
                            _refreshing.onNext(false)
                        }
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