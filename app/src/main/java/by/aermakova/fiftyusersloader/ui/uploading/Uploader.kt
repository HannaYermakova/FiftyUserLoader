package by.aermakova.fiftyusersloader.ui.uploading

import android.content.Context
import by.aermakova.fiftyusersloader.util.getRequestBody
import by.aermakova.fiftyusersloader.util.prepareFilePart
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Uploader constructor(
    private val manager: UploadManager,
    private val userId: Int
) {
    private val disposable = CompositeDisposable()

    fun startUploading(fileTitle: String?, applicationContext: Context) {
        if (fileTitle != null) {
            val body = applicationContext.getRequestBody(fileTitle)
            disposable.add(
                body.progressSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { manager.updateNotification(it.toInt(), userId) },
                        { it.printStackTrace() }
                    )
            )
            disposable.add(
                manager.getInteractor().uploadFile(prepareFilePart(fileTitle, body))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { manager.clearNotification(userId) },
                        { it.printStackTrace() }
                    )
            )
        }
    }

    fun clearDisposable() = disposable.clear()
}