package by.aermakova.fiftyusersloader.ui.user

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.util.downloadFile
import by.aermakova.fiftyusersloader.util.fileIsSaved
import by.aermakova.fiftyusersloader.util.getRequestBody
import by.aermakova.fiftyusersloader.util.prepareFilePart
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class UserViewModel @ViewModelInject constructor(
    private val userInteractor: UserInteractor,
    private val appContext: Context
) :
    ViewModel() {

    var currentUserId: Int = -1

    val disposable = CompositeDisposable()

    private var _toastText = PublishSubject.create<String>()
    val toastText: Observable<String>
        get() = _toastText.hide()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private var _pictureIsSaved = PublishSubject.create<Boolean>()
    val pictureIsSaved: Observable<Boolean>
        get() = _pictureIsSaved.hide()

    val saveFile = { downloadFile() }

    private fun downloadFile() {
        val currentUser = user.value
        if (currentUser != null) {
            with(currentUser) {
                disposable.add(
                    Single.create<Boolean> { emitter ->
                        val loaded = appContext.downloadFile(picture, login, imageExtension)
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(loaded)
                        }
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                _toastText.onNext(
                                    appContext.getString(
                                        R.string.file_downloaded,
                                        login
                                    )
                                )
                                _pictureIsSaved.onNext(true)
                            },
                            {
                                it.printStackTrace()
                                _pictureIsSaved.onNext(false)
                            }
                        )
                )
            }
        }
    }

    fun loadUser() {
        disposable.add(
            userInteractor.getUserById(currentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _user.value = it
                        checkFile(it)
                    },
                    { it.printStackTrace() }
                )
        )
    }

    private fun checkFile(user: User) {
        disposable.add(
            Single.create<Boolean> {
                val fileSaved =
                    appContext.fileIsSaved(user.login, user.imageExtension)
                it.onSuccess(fileSaved)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { _pictureIsSaved.onNext(it) },
                    { it.printStackTrace() }
                )
        )
    }

    val upload = { uploadFile() }

    private fun uploadFile() {
        val currentUser = user.value
        if (currentUser != null) {
            with(currentUser) {
                val body = appContext.getRequestBody(login, imageExtension)
                disposable.add(
                    body.progressSubject.subscribeOn(Schedulers.io())
                        .subscribe(
                            { Log.i("UserViewModel", it.toString()) },
                            { it.printStackTrace() }
                        )
                )

                disposable.add(
                    userInteractor.uploadFile(prepareFilePart(login, body))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                _toastText.onNext(
                                    appContext.getString(
                                        R.string.file_uploaded,
                                        login
                                    )
                                )
                            },
                            { it.printStackTrace() }
                        )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}