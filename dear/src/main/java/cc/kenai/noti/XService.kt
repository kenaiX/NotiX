package cc.kenai.noti

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import cc.kenai.noti.events.RulesChanged
import cc.kenai.noti.model.NotiType
import cc.kenai.noti.model.NotificationFilter
import cc.kenai.noti.model.RulesFactory
import cc.kenai.noti.model.SuggestManager
import cc.kenai.noti.utils.NotiHelperUtil
import cc.kenai.noti.utils.log
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class XService : NotificationListenerService() {

    private val mKeyMap = HashMap<String, NotiType>()

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (NotiHelperUtil.ACTION_CANCEL_RING == action) {
                cancelNotifyForce()
            } else if (NotiHelperUtil.ACTION_ALARM == action) {
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

        val notiType = NotificationFilter.needNoti(sbn)

        if (notiType != null) {
            val key = sbn2Key(sbn)
            if (!mKeyMap.contains(key)) {
                mKeyMap.put(key, notiType)
            }
            notifyIfNeed()
        } else {
            SuggestManager.add(NotificationFilter.buildRule(sbn))
        }

        (applicationContext as XApplication).xServiceCount = ++mPostedCount
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) {
            return
        }
        log("onNotificationRemoved");
        val key = sbn2Key(sbn)
        if (mKeyMap.contains(key)) {
            mKeyMap.remove(key)
            cancelNotifyIfNeed()
        }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationFilter.setupRules(RulesFactory.loadRules(this))
        val filter = IntentFilter(NotiHelperUtil.ACTION_CANCEL_RING)
        filter.addAction(NotiHelperUtil.ACTION_ALARM)
        registerReceiver(mReceiver, filter)
        RxBus.get().register(this)

    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        val activeNotifications = getActiveNotifications()
        if (activeNotifications != null && activeNotifications.size > 0) {
            for (sbn in activeNotifications) {
                SuggestManager.clear()
                SuggestManager.add(NotificationFilter.buildRule(sbn))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this)
        unregisterReceiver(mReceiver)
    }

    @Subscribe
    fun onSaveRules(event: RulesChanged) {
        val sb = StringBuilder()
        event.rule.forEach {
            sb.append(RulesFactory.rule2json(it)).append("\n")
        }
        getSharedPreferences("config", 0).edit().putString("rules", sb.toString()).apply()
        NotificationFilter.setupRules(event.rule)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand");
        return Service.START_STICKY
    }

    private fun sbn2Key(sbn: StatusBarNotification): String {
        return sbn.key
    }

    private var mSubscribe: Disposable? = null

    private fun notifyIfNeed() {
        if (mKeyMap.size == 0) {
            return
        }

        var needNoti = false
        var needLoop = false
        var needRing = false
        for ((_, v) in mKeyMap) {
            if (!needNoti && v.needNoti) {
                needNoti = true
            }
            if (!needLoop && v.needLoop) {
                needLoop = true
            }
            if (!needRing && v.needRing) {
                needRing = true
            }
        }

        if (mSubscribe == null && needLoop) {
            log("start loop notify")
            mSubscribe = Observable.interval(30, 30, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (mKeyMap.size > 0) {
                            NotiHelperUtil.ring(applicationContext)
                        } else {
                            cancelNotifyForce()
                        }
                    })
            NotiHelperUtil.alarm(applicationContext)
        }

        if (needNoti) {
            log("start notify")
            NotiHelperUtil.ring(applicationContext)
        }

        if (needRing && !NotiHelperUtil.existAlarm()) {
            log("start ring")
            NotiHelperUtil.alarm(applicationContext)
        }
    }

    private fun cancelNotifyIfNeed() {
        if (mKeyMap.size == 0) {
            log("stop notify")
            cancelNotifyForce()
        }
    }

    private fun cancelNotifyForce() {
        log("stop notify force")
        mSubscribe?.dispose()
        mSubscribe = null
        mKeyMap.clear()

        NotiHelperUtil.cancelRing(applicationContext)
        NotiHelperUtil.cancelAlarm(applicationContext)
        NotiHelperUtil.stopPlayAlarm()
    }
}