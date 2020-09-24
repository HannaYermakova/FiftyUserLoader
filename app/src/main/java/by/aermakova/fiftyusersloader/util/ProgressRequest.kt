package by.aermakova.fiftyusersloader.util

import android.content.Context
import android.net.Uri
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequest(
    private val context: Context,
    private val uri: Uri,
    private val file: File
) : RequestBody() {

    companion object {
        const val DEFAULT_BUFFER_SIZE = 2048
        const val DEFAULT_TIME = 1
    }

    private val _progressSubject = PublishSubject.create<Float>()
    val progressSubject: Observable<Float>
        get() = _progressSubject

    override fun contentType(): MediaType? {
        return context.contentResolver.getType(uri)?.toMediaTypeOrNull()
    }

    var numWrite = 0

    override fun writeTo(sink: BufferedSink) {
        ++numWrite
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        inputStream.use { stream ->
            var read: Int
            var lastProgress = 0.0f
            read = stream.read(buffer)
            while (read != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                read = stream.read(buffer)
                if (numWrite > DEFAULT_TIME) {
                    val progress = (uploaded.toFloat() / fileLength.toFloat()) * 100f
                    if (progress - lastProgress > 1 || progress == 100f) {
                        _progressSubject.onNext(progress)
                        lastProgress = progress
                    }
                }
            }
        }
    }
}