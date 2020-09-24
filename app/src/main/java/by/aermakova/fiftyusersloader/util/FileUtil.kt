package by.aermakova.fiftyusersloader.util

import android.content.Context
import android.net.Uri
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.*
import java.net.URL
import java.net.URLConnection

const val FOLDER_NAME = "temp"
const val FILE_FIELD = "file"

fun Context.downloadFile(path: String?, fileName: String, extension: String): Boolean {

    try {
        val url = URL(path)
        val urlConnection: URLConnection = url.openConnection().apply {
            readTimeout = 5000
            connectTimeout = 10000
        }
        val inputStream: InputStream = urlConnection.getInputStream()
        val inStream = BufferedInputStream(inputStream, 1024 * 5)
        val file = createFile(filesDir.path, FOLDER_NAME, "$fileName.$extension")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val outStream = FileOutputStream(file)
        val buff = ByteArray(5 * 1024)
        var len: Int
        while (inStream.read(buff).also { len = it } != -1) {
            outStream.write(buff, 0, len)
        }
        outStream.flush()
        outStream.close()
        inStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

fun createFile(dir: String, folder: String, fileTitle: String): File {
    val directory = File(dir, folder).apply { mkdir() }
    return File(directory, fileTitle)
}

private fun deleteFolder(dir: String, folder: String): Boolean {
    val directory = File(dir, folder)
    deleteRecursive(directory)
    return directory.delete()
}

fun deleteRecursive(fileOrDirectory: File) {
    if (fileOrDirectory.isDirectory) {
        for (child in fileOrDirectory.listFiles()!!) {
            deleteRecursive(child)
        }
    }
    fileOrDirectory.delete()
}

fun Context.deletePhotoFolder(): Boolean {
    return deleteFolder(filesDir.path, FOLDER_NAME)
}

fun Context.fileIsSaved(fileName: String, extension: String): Boolean {
    val file = createFile(filesDir.path, FOLDER_NAME, "$fileName.$extension")
    return file.exists()
}

fun prepareFilePart(fileName: String, requestBody: RequestBody): MultipartBody.Part {
    return MultipartBody.Part.createFormData(FILE_FIELD, fileName, requestBody)
}

fun Context.getRequestBody(fileName: String): ProgressRequest {
    val file = createFile(filesDir.path, FOLDER_NAME, fileName)
    val uri = Uri.fromFile(file)
    return ProgressRequest(context = this, uri = uri, file = file)
}