package cc.kenai.noti.utils

import android.annotation.SuppressLint
import android.app.*
import android.app.AlarmManager.ELAPSED_REALTIME
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import cc.kenai.noti.R
import cc.kenai.noti.events.RingSoundChangeEvent
import cc.kenai.noti.view.RingView
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe

@SuppressLint("StaticFieldLeak")
object NotiHelperUtil {
    const val ACTION_CANCEL_RING = "cc.kenai.noti.action.CANCEL_RING"
    const val ACTION_ALARM = "cc.kenai.noti.action.ALARM"

    private const val CHANNEL_ID = "notix_no_sound"
    private const val CHANNEL_NAME = "notix_no_sound"

    private var mNM: NotificationManagerCompat? = null

    private var mAM: AlarmManager? = null
    private lateinit var mAudioManager: AudioManager

    private var mPlayer: MediaPlayer? = null
    private var mNotiPlayer = MediaPlayer()

    private var mVibrator: Vibrator? = null

    private lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context.applicationContext
        mAudioManager = context.getSystemService(AudioManager::class.java)
        mNotiPlayer.setOnCompletionListener { mPlayingNotiFlag = false }

        updateNotiPlayer(null)

        RxBus.get().register(this)
    }

    @Subscribe
    fun updateNotiPlayer(event: RingSoundChangeEvent?) {
        mNotiPlayer.reset()
        mNotiPlayer.setDataSource(mContext, ConfigHelper.ringUri)
        mNotiPlayer.isLooping = false
        mNotiPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
        try {
            mNotiPlayer.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun buildForgroundNoti(context: Context): Notification {
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(context)
        }

        createNotiChannel(context)

        return NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("running")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_status)
                .setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), 0))
                .build()
    }


    fun sendCancelNotification() {
        Log.e("@@@@", "ring")
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(mContext)
        }
        createNotiChannel(mContext)
        val notification = NotificationCompat.Builder(mContext, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("点击取消提醒")
                .setContentText("点击取消提醒")
                .setSmallIcon(R.drawable.ic_status)
                //.setAutoCancel(true)
                //.setOngoing(true)
                .setOnlyAlertOnce(false)
                .setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, Intent(ACTION_CANCEL_RING), 0))
                .setContentIntent(PendingIntent.getBroadcast(mContext, 0, Intent(ACTION_CANCEL_RING), 0))
                .build()
        mNM?.notify("notify", 110, notification)
        vibrate(mContext)
    }

    fun ring(context: Context) {
        playNoti(context)
    }

    fun vibrate(context: Context) {
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

    private var existAlarm = false
    fun alarm(context: Context) {
        Log.e("@@@@", "alarm")
        if (mAM == null) {
            mAM = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        existAlarm = true
        mAM?.setExactAndAllowWhileIdle(ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 3 * 60 * 1000,
                PendingIntent.getBroadcast(context, 0, Intent(ACTION_ALARM), FLAG_UPDATE_CURRENT))
    }

    fun cancelAlarm(context: Context) {
        Log.e("@@@@", "cacel alarm")
        if (mAM == null) {
            mAM = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        existAlarm = false
        mAM?.cancel(PendingIntent.getBroadcast(context, 0, Intent(ACTION_ALARM), FLAG_UPDATE_CURRENT))
    }

    fun existAlarm() = existAlarm

    private var mPlayingNotiFlag = false

    fun playNoti(context: Context) {
        if (mPlayingNotiFlag) {
            return
        }
        mPlayingNotiFlag = true
        setVolume(ConfigHelper.volume)
        mNotiPlayer.start()
    }

    fun playAlarm(context: Context) {
        if (mPlayer != null) {
            return
        }

        mPlayer = MediaPlayer()
        mPlayer?.setDataSource(context, getSystemDefultRingtoneUri(context))
        mPlayer?.isLooping = true
        mPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)
        setVolume(ConfigHelper.volume)
        try {
            mPlayer?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mPlayer?.start()
        showAlarm(context)
    }

    fun stopPlayAlarm(context: Context) {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
        hideAlarm(context)
    }

    var mAlarmView: View? = null

    fun showAlarm(context: Context) {
        val temp = RingView(context)
        temp.setText("点击停止响铃")
        temp.setTextColor(Color.WHITE)
        temp.setBackgroundColor(Color.BLACK)
        temp.gravity = Gravity.CENTER
        temp.setOnClickListener {
            stopPlayAlarm(it.context)
        }
        temp.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                stopPlayAlarm(v.context)
                return true
            }
        })

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR)
        layoutParams.title = "Noti X"

        val metrics = context.getResources().getDisplayMetrics()
        layoutParams.width = metrics.widthPixels
        layoutParams.height = metrics.heightPixels

        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.CENTER
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        //layoutParams.meizuParams.flags |= MeizuLayoutParams.MEIZU_FLAG_DISABLE_HIDING_ON_FULL_SCREEN;

        try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.addView(temp, layoutParams)
            mAlarmView = temp
        } catch (e: Exception) {
        }
    }

    fun hideAlarm(context: Context) {
        if (mAlarmView != null) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.removeView(mAlarmView)
            mAlarmView = null
        }
    }

    fun testNoti(context: Context) {
        Log.e("@@@@", "test")
        if (mNM == null) {
            mNM = NotificationManagerCompat.from(context)
        }

        createNotiChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("testTitle")
                .setContentText("testText")
                .setSmallIcon(R.drawable.ic_status)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getBroadcast(context, 0, Intent("test"), 0))
                .build()
        mNM?.notify("notify", 999, notification)
    }

    private fun getSystemDefultRingtoneUri(context: Context): Uri {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
    }

    private fun createNotiChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    private fun setVolume(scale: Float) {
        var max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        var volume = (scale * max).toInt()
        if (volume < 1) {
            volume = 1
        } else if (volume > max) {
            volume = max
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
    }

    private fun getVolumeScale(): Float {
        var volume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        var max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        if (volume == 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 1, 0)
            volume = 1;
        }
        val scale = volume.toFloat() / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        return scale
    }
}