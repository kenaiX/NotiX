package cc.kenai.dear

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import cc.kenai.dear.model.NotificationFilter
import cc.kenai.dear.utils.NotiHelperUtil
import cc.kenai.dear.utils.log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class DearService : NotificationListenerService() {

    private val mNotificationFilter = NotificationFilter();

    private val mKeyList = ArrayList<String>()

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if(NotiHelperUtil.ACTION_CANCEL_RING == action) {
                cancelNotifyForce()
            }else if(NotiHelperUtil.ACTION_ALARM == action){
                NotiHelperUtil.playAlarm(applicationContext)
            }
        }

    }

    private var mPostedCount = 0L

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) {
            return
        }

        log("onNotificationPosted");

        if (mNotificationFilter.needNoti(sbn)) {
            val key = sbn2Key(sbn)
            if (!mKeyList.contains(key)) {
                mKeyList.add(key)
            }
            notifyIfNeed()
        }

        (applicationContext as DearApplication).dearServiceCount = ++mPostedCount
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) {
            return
        }

        log("onNotificationRemoved");

        val key = sbn2Key(sbn)
        if (mKeyList.contains(key)) {
            mKeyList.remove(key)
            cancelNotifyIfNeed()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(NotiHelperUtil.ACTION_CANCEL_RING)
        filter.addAction(NotiHelperUtil.ACTION_ALARM)
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand");
        return Service.START_STICKY
    }

    private fun sbn2Key(sbn: StatusBarNotification): String {
        return sbn.packageName + ":" + sbn.id
    }

    var mSubscribe: Disposable? = null

    private fun notifyIfNeed() {
        if (mSubscribe == null && mKeyList.size > 0) {
            log("start notify")
            mSubscribe = Observable.interval(0, 30, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (mKeyList.size > 0) {
                            NotiHelperUtil.ring(applicationContext)
                        }
                    })
            NotiHelperUtil.alarm(applicationContext )
        }
    }

    private fun cancelNotifyIfNeed() {
        if (mKeyList.size == 0) {
            log("stop notify")
            cancelNotifyForce()
        }
    }

    private fun cancelNotifyForce() {
        log("stop notify force")
        mSubscribe?.dispose()
        mSubscribe = null
        mKeyList.clear();

        NotiHelperUtil.cancelRing(applicationContext)
        NotiHelperUtil.cancelAlarm(applicationContext )
        NotiHelperUtil.stopPlayAlarm()
    }
}