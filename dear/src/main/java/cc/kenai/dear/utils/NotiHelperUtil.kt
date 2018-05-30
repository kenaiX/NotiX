package cc.kenai.dear.utils

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
import cc.kenai.dear.R
import android.media.RingtoneManager


object NotiHelperUtil {
    const val ACTION_CANCEL_RING = "cc.kenai.dear.action.CANCEL_RING"
    const val ACTION_ALARM = "cc.kenai.dear.action.ALARM"

    var mNM: NotificationManager? = null

    var mAM: AlarmManager? = null

    var mPlayer: MediaPlayer? = null

    fun ring(context: Context) {
        Log.e("@@@@", "ring")
        if (mNM == null) {
            mNM = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        val notification = Notification.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("新通知")
                .setContentText("这是一条重要的消息")
                .setAutoCancel(true)
                .setVibrate(longArrayOf(100, 100, 100, 100, 100, 100))
                .setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(ACTION_CANCEL_RING), 0))
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring))
                .build();
        mNM?.notify("notify", 110, notification)
    }

    fun cancelRing(context: Context) {
        Log.e("@@@@", "cancel ring")
        if (mNM == null) {
            mNM = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun getSystemDefultRingtoneUri(context: Context): Uri {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
    }
}