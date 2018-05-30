package cc.kenai.dear.utils

import android.app.Activity
import android.app.Service
import android.util.Log

fun Activity.log(string: String){
    Log.i("DearActivity",string);
}

fun Service.log(string: String){
    Log.i("DearService",string);
}