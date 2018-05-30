package cc.kenai.projectX.rxjava

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import rx.Observable
import rx.functions.Action1
import java.util.concurrent.TimeUnit
import android.os.Looper
import rx.android.schedulers.AndroidSchedulers


class RxjavaTest :Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("@@@@","start at "+SystemClock.elapsedRealtime());
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action1 {
                    Log.e("@@@@","do at "+SystemClock.elapsedRealtime());
                    checkThread()
                })
        Log.e("@@@@","start at "+SystemClock.elapsedRealtime());
        Log.e("@@@@","start at "+SystemClock.elapsedRealtime());
        Log.e("@@@@","start at "+SystemClock.elapsedRealtime());
        Log.e("@@@@","start at "+SystemClock.elapsedRealtime());
    }

    fun checkThread() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw RuntimeException("DataFetchHelper can not run on main thread.")
        }
    }
}