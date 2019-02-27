package cc.kenai.noti

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

class WatchDogService : Service() {
    val CHANNEL_ID = "watchdog_service"
    val CHANNEL_NAME = "watchdog_service"

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            notification = Notification.Builder(this, CHANNEL_ID).build()
        } else {
            notification = Notification.Builder(this).build()
        }
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}