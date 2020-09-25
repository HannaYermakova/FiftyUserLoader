package by.aermakova.fiftyusersloader.ui.user

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import by.aermakova.fiftyusersloader.R

private const val PROGRESS_UPDATE_ACTION = "by.aermakova.fiftyusersloader.progress_update"
private const val PROGRESS_UPDATE = "progress_update"

class UploadReceiver(private val listener: OnProgressUpdate) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val progress = intent?.getIntExtra(PROGRESS_UPDATE, -1)
        if (progress != null && progress > 0) {
            listener.progressUpdate(progress)
        } else listener.showErrorMessage()
    }

    companion object {
        fun progressUpdate(context: Context, userId: Int, progress: Int) {
            val intent = Intent("$PROGRESS_UPDATE_ACTION$userId").apply {
                putExtra(PROGRESS_UPDATE, progress)
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        fun showErrorMessage(context: Context, userId: Int) {
            val intent = Intent("$PROGRESS_UPDATE_ACTION$userId")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    fun registerReceiver(context: Context, userId: Int) {
        val filter = IntentFilter("$PROGRESS_UPDATE_ACTION$userId")
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter)
    }

    fun unregisterReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
    }
}

interface OnProgressUpdate {
    fun progressUpdate(progress: Int)
    fun showErrorMessage()
}