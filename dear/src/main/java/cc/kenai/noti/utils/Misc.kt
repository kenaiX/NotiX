package cc.kenai.noti.utils

import android.app.Activity
import android.app.Service
import android.util.Log

fun Activity.log(string: String) {
    Log.i("SettingsActivity", string);
}

fun Service.log(string: String) {
    Log.i("XService", string);
}