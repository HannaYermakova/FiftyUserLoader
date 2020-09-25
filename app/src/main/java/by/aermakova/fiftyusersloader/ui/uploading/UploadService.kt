package by.aermakova.fiftyusersloader.ui.uploading

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.ui.main.DEF_USER_ID
import by.aermakova.fiftyusersloader.ui.main.MainActivity
import by.aermakova.fiftyusersloader.ui.user.UploadReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UploadService : Service(), UploadManager {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val CHANNEL_NAME = "Uploading Service Channel"
        const val CURRENT_USER_ID = "uploaded_user_id"
        const val FILE_TITLE = "file_title"
        const val USER_ID = "user_id"
        const val MAX_PROGRESS = 100
        const val MIN_PROGRESS = 0
    }

    @Inject
    lateinit var userInteractor: UserInteractor
    private var loader: Uploader? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fileTitle = intent?.getStringExtra(FILE_TITLE)
        val userId = intent?.getIntExtra(USER_ID, DEF_USER_ID)
        if (fileTitle != null && userId != null && userId > DEF_USER_ID) {
            loader = Uploader(this, userId, applicationContext).apply { startUploading(fileTitle) }
            notificationBuilder = createNotification(fileTitle, userId)
            startForeground(userId, notificationBuilder?.build())
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(title: String, userId: Int): NotificationCompat.Builder {
        val resultIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(CURRENT_USER_ID, userId)
        }
        val resultPendingIntent = PendingIntent.getActivity(
            this, userId, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text = getString(R.string.file_uploading, title)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.female)
            .setTicker(text)
            .setContentTitle(text)
            .setContentIntent(resultPendingIntent)
            .setWhen(System.currentTimeMillis())
            .setOnlyAlertOnce(true)
            .setProgress(MAX_PROGRESS, MIN_PROGRESS, false)
    }

    override fun updateNotification(
        progress: Int,
        userId: Int,
        fileTitle: String
    ) {
        val text = getString(R.string.file_uploading, fileTitle)
        if (notificationBuilder != null) {
            notificationBuilder
                ?.setContentTitle(text)
                ?.setProgress(MAX_PROGRESS, progress, false)
                ?.setContentText("$progress%")
            notificationManager?.notify(userId, notificationBuilder!!.build())
        }
        UploadReceiver.progressUpdate(this, userId, progress)
    }

    override fun clearNotification(userId: Int) {
        stopForeground(false)
        notificationManager?.cancel(userId)
    }

    override fun getInteractor(): UserInteractor {
        return userInteractor
    }

    override fun showErrorMessage(userId: Int) {
        if(notificationBuilder != null) {
            notificationBuilder
                ?.setContentTitle(resources.getString(R.string.error_message_notification))
                ?.setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
            notificationManager?.notify(userId, notificationBuilder!!.build())
            UploadReceiver.showErrorMessage(this, userId)
        }
    }

    override fun onDestroy() {
        Uploader.clearDisposable()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}