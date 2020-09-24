package by.aermakova.fiftyusersloader.ui.uploading

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.UserInteractor
import by.aermakova.fiftyusersloader.ui.DEF_USER_ID
import by.aermakova.fiftyusersloader.ui.MainActivity
import by.aermakova.fiftyusersloader.ui.user.PROGRESS_UPDATE
import by.aermakova.fiftyusersloader.ui.user.PROGRESS_UPDATE_ACTION
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UploadingService : Service(), UploadCondition {

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
    private lateinit var loader: Uploader
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fileTitle = intent?.getStringExtra(FILE_TITLE)
        val userId = intent?.getIntExtra(USER_ID, DEF_USER_ID)
        if (fileTitle != null && userId != null && userId > DEF_USER_ID) {
            loader = Uploader(this, userId)
            loader.startUploading(fileTitle, applicationContext)
            notificationBuilder = createNotification(fileTitle, userId) ?: throw Exception()
            startForeground(userId, notificationBuilder.build())
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(title: String, userId: Int): Notification.Builder? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.putExtra(CURRENT_USER_ID, userId)

        val resultPendingIntent = PendingIntent.getActivity(
            this, userId, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text = applicationContext.getString(R.string.file_uploading, title)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.female)
                .setTicker(text)
                .setContentTitle(text)
                .setContentIntent(resultPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setProgress(MAX_PROGRESS, MIN_PROGRESS, false)
        } else null
    }

    override fun updateNotification(
        progress: Int,
        userId: Int
    ) {
        notificationBuilder.setProgress(MAX_PROGRESS, progress, false).setContentText("$progress%")
        notificationManager.notify(userId, notificationBuilder.build())

        val intent = Intent("$PROGRESS_UPDATE_ACTION$userId").apply {
            putExtra(PROGRESS_UPDATE, progress)
        }
        applicationContext.sendBroadcast(intent)
    }

    override fun clearNotification(userId: Int) {
        stopForeground(false)
        notificationManager.cancel(userId)
    }

    override fun getInteractor(): UserInteractor {
        return userInteractor
    }

    override fun onDestroy() {
        super.onDestroy()
        loader.clearDisposable()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

interface UploadCondition {
    fun updateNotification(progress: Int, userId: Int)
    fun clearNotification(userId: Int)
    fun getInteractor(): UserInteractor
}