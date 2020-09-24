package by.aermakova.fiftyusersloader.ui.user

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.ui.DEF_USER_ID
import by.aermakova.fiftyusersloader.ui.uploading.UploadingService
import by.aermakova.fiftyusersloader.ui.uploading.UploadingService.Companion.FILE_TITLE
import by.aermakova.fiftyusersloader.ui.uploading.UploadingService.Companion.USER_ID
import by.aermakova.fiftyusersloader.util.downloadFile
import by.aermakova.fiftyusersloader.util.fileIsSaved
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

const val PROGRESS_UPDATE = "progress_update"
const val PROGRESS_UPDATE_ACTION = "by.aermakova.fiftyusersloader.progress_update"

class UserViewModel @ViewModelInject constructor(
    private val userInteractor: UserInteractor,
    private val appContext: Context
) :
    ViewModel() {

    var currentUserId: Int = DEF_USER_ID
        set(value) {
            field = value
            loadUser()
            registerReceiver()
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

    private var broadcastReceiver: BroadcastReceiver = UploadReceiver()

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
                                _toastText.onNext(
                                    appContext.getString(
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
                val intent = Intent(appContext, UploadingService::class.java).apply {
                    putExtra(FILE_TITLE, "$login.$imageExtension")
                    putExtra(USER_ID, id)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext.startForegroundService(intent)
                } else {
                    appContext.startService(intent)
                }
                registerReceiver()
            }
        }
    }

    override fun onCleared() {
        unregisterReceiver()
        disposable.clear()
        super.onCleared()
    }

    inner class UploadReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val progress = intent?.getIntExtra(PROGRESS_UPDATE, 0) ?: 0
            if (!disposable.isDisposed) {
                _progressSubject.onNext(progress)
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter("$PROGRESS_UPDATE_ACTION$currentUserId")
        appContext.registerReceiver(broadcastReceiver, filter)
    }

    private fun unregisterReceiver() {
        appContext.unregisterReceiver(broadcastReceiver)
    }
}
