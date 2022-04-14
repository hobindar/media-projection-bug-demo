package ca.hobin.mediaprojectionbugdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 374180 // Just a random number, no special meaning, must be unique

class MediaProjectionForegroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(NOTIFICATION_ID, getNotification())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    private fun getNotification(): Notification {
        val title = "MediaProjection Bug Demo"
        val description = "MediaProjection is running"
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, getNotificationChannel())
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker("$title - $description")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(false)
                .setDefaults(0)
        return builder.build()
    }

    private fun getNotificationChannel(): String {
        val id = "foregroundService"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(id) == null) {
                val name = "Foreground service notifications"
                val channel = NotificationChannel(id, name, IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
        }
        return id
    }
}
