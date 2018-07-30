package cc.kenai.noti.utils

import android.app.*
import android.app.AlarmManager.ELAPSED_REALTIME
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import cc.kenai.noti.R
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.os.Vibrator


object NotiHelperUtil {
    const val ACTION_CANCEL_RING = "cc.kenai.noti.action.CANCEL_RING"
    const val ACTION_ALARM = "cc.kenai.noti.action.ALARM"

    private const val CHANNEL_ID = "important_msg_ring_0"
    private const val CHANNEL_NAME = "important_msg_ring"

    var mNM: NotificationManagerCompat? = null

    var mAM: AlarmManager? = null

    var mPlayer: MediaPlayer? = null

    var mVibrator: Vibrator? = null

    fun ring(context: Context) {
        Log.e("@@@@", "ring")
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(context)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring), null)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("点击取消响铃")
                .setContentText("点击取消响铃")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(ACTION_CANCEL_RING), 0))
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring))
                .build()
        mNM?.notify("notify", 110, notification)

        if (mVibrator == null) {
            mVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        mVibrator?.vibrate(longArrayOf(0, 666), -1)
    }

    fun cancelRing(context: Context) {
        Log.e("@@@@", "cancel ring")
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(context)
        }
        mNM?.cancel("notify", 110)
    }

    fun alarm(context: Context) {
        Log.e("@@@@", "alarm")
        if (mAM == null) {
            mAM = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        mAM?.setExactAndAllowWhileIdle(ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 3 * 60 * 1000,
                PendingIntent.getBroadcast(context, 0, Intent(ACTION_ALARM), FLAG_UPDATE_CURRENT))
    }

    fun cancelAlarm(context: Context) {
        Log.e("@@@@", "cacel alarm")
        if (mAM == null) {
            mAM = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        mAM?.cancel(PendingIntent.getBroadcast(context, 0, Intent(ACTION_ALARM), FLAG_UPDATE_CURRENT))
    }

    fun playAlarm(context: Context) {
        if (mPlayer != null) {
            return
        }

        mPlayer = MediaPlayer()
        mPlayer?.setDataSource(context, getSystemDefultRingtoneUri(context))
        mPlayer?.isLooping = true
        mPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)
        try {
            mPlayer?.prepare()
        } catch (e: Exception) {
        }
        mPlayer?.start()
    }

    fun stopPlayAlarm() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }

    fun testNoti(context: Context){
        Log.e("@@@@", "test")
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(context)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring), null)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("testTitle")
                .setContentText("testText")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getBroadcast(context, 0, Intent("test"), 0))
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring))
                .build()
        mNM?.notify("notify", 999, notification)
    }

    private fun getSystemDefultRingtoneUri(context: Context): Uri {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
    }
}