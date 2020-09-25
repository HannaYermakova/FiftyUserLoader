package by.aermakova.fiftyusersloader.ui.uploading

import android.content.Context
import android.util.Log
import by.aermakova.fiftyusersloader.util.getRequestBody
import by.aermakova.fiftyusersloader.util.prepareFilePart
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Uploader constructor(
    private val manager: UploadManager,
    private val userId: Int,
    private val applicationContext: Context
) {

    companion object {
        private val disposable = CompositeDisposable()
        fun clearDisposable() = disposable.clear()
    }

    fun startUploading(fileTitle: String) {
        val body = applicationContext.getRequestBody(fileTitle)
        disposable.add(
            body.progressSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { manager.updateNotification(it.toInt(), userId, fileTitle) },
                    {
                        it.printStackTrace()
                        manager.showErrorMessage(userId)
                    }
                )
        )
        disposable.add(
            manager.getInteractor().uploadFile(prepareFilePart(fileTitle, body))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { manager.clearNotification(userId) },
                    {
                        it.printStackTrace()
                        manager.showErrorMessage(userId)
                    }
                )
        )
    }
}