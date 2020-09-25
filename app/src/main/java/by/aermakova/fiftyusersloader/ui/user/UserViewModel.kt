package by.aermakova.fiftyusersloader.ui.user

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.ui.main.DEF_USER_ID
import by.aermakova.fiftyusersloader.ui.uploading.UploadService
import by.aermakova.fiftyusersloader.ui.uploading.UploadService.Companion.FILE_TITLE
import by.aermakova.fiftyusersloader.ui.uploading.UploadService.Companion.USER_ID
import by.aermakova.fiftyusersloader.util.downloadFile
import by.aermakova.fiftyusersloader.util.fileIsSaved
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
    ViewModel(), OnProgressUpdate {

    private var broadcastReceiver = UploadReceiver(this)

    var currentUserId: Int = DEF_USER_ID
        set(value) {
            field = value
            loadUser()
            broadcastReceiver.registerReceiver(appContext, value)
        }

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

    private var _progressSubject = PublishSubject.create<Int>()
    val progressSubject: Observable<Int>
        get() = _progressSubject

    private var _errorMessageSubject = PublishSubject.create<String>()
    val errorMessageSubject: Observable<String>
        get() = _errorMessageSubject

    val saveFile = { downloadFile() }

    private fun downloadFile() {
        val currentUser = user.value
        if (currentUser != null) {
            with(currentUser) {
                disposable.add(
                    Single.create<Boolean> { emitter ->
                        val loaded =
                            appContext.downloadFile(/*picture*/"https://vk.com/doc30142712_567416456?hash=199ef0c873cbb2825d&dl=1870ac69144ba31f62",
                                login, /*imageExtension*/
                                "pdf"
                            )
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(loaded)
                        }
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                _toastText.onNext( appContext.getString(
                                        R.string.file_downloaded,
                                        login
                                    )
                                )
                                _pictureIsSaved.onNext(true)
                            },
                            {
                                _pictureIsSaved.onNext(false)
                                it.printStackTrace()
                            }
                        )
                )
            }
        }
    }

    private fun loadUser() {
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
                val fileSaved = appContext.fileIsSaved(user.login, user.imageExtension)
                if (!it.isDisposed) {
                    it.onSuccess(fileSaved)
                }
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
                val intent = Intent(appContext, UploadService::class.java).apply {
                    putExtra(FILE_TITLE, "$login.$imageExtension")
                    putExtra(USER_ID, id)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext.startForegroundService(intent)
                } else {
                    appContext.startService(intent)
                }
            }
        }
    }

    override fun onCleared() {
        broadcastReceiver.unregisterReceiver(appContext)
        disposable.clear()
        Log.d("A_UserViewModel", "disposable.clear()")
        super.onCleared()
    }

    override fun progressUpdate(progress: Int) {
        _progressSubject.onNext(progress)
    }

    override fun showErrorMessage() {
        _errorMessageSubject.onNext(appContext.resources.getString(R.string.error_message_notification))
    }
}