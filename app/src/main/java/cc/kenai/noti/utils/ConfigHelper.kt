package cc.kenai.noti.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import cc.kenai.noti.R
import cc.kenai.noti.events.RingSoundChangeEvent
import com.hwangjr.rxbus.RxBus

object ConfigHelper {
    const val RESULT_ACTION_CHANGE_RING = 10001

    lateinit var preferences: SharedPreferences
    var volume = 0.5f
        set(value) {
            preferences.edit().putFloat("volume", value).apply()
            field = value
        }
    lateinit var ringUri: Uri
    lateinit var defaultUri: Uri

    fun init(context: Context) {
        preferences = context.getSharedPreferences("config", 0)
        volume = preferences.getFloat("volume", 0.5f)
        defaultUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring)
        val uri = preferences.getString("ring_uri", null)
        if (uri == null) {
            ringUri = defaultUri
        } else {
            ringUri = Uri.parse(uri)
        }
    }

    fun changeRingSound(context: Activity) {
        //打开系统铃声设置
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        //设置铃声类型和title
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择提示音")
        //当设置完成之后返回到当前的Activity
        context.startActivityForResult(intent, ConfigHelper.RESULT_ACTION_CHANGE_RING)
    }

    fun onRingChangeReturn(uri: Uri) {
        ringUri = uri
        preferences.edit().putString("ring_uri", uri.toString()).apply()
        Log.e("@@@@", uri.toString())
        RxBus.get().post(RingSoundChangeEvent())
    }
}