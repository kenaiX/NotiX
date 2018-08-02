package cc.kenai.noti.utils

import android.content.Context
import android.content.SharedPreferences

object ConfigHelper {
    lateinit var preferences: SharedPreferences
    var volume = 0.5f
        set(value) {
            preferences.edit().putFloat("volume", value).apply()
            field = value
        }

    fun init(context: Context) {
        preferences = context.getSharedPreferences("config", 0)

        volume = preferences.getFloat("volume", 0.5f)
    }


}