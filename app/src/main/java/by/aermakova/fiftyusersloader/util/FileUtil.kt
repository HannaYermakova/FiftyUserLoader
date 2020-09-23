package by.aermakova.fiftyusersloader.util

import android.content.Context
import android.net.Uri
import android.os.FileUtils
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

fun Context.deletePhotoFolder() : Boolean{
  return  deleteFolder(filesDir.path, FOLDER_NAME)
}

fun Context.fileIsSaved(fileName: String, extension: String): Boolean {
    val file = createFile(filesDir.path, FOLDER_NAME, "$fileName.$extension")
    return file.exists()
}

fun Context.prepareFilePart(fileName: String, extension: String): MultipartBody.Part {
    val file = createFile(filesDir.path, FOLDER_NAME, "$fileName.$extension")
    val uri = Uri.fromFile(file)
    val requestFile: RequestBody =file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(FILE_FIELD, file.name, requestFile)
}