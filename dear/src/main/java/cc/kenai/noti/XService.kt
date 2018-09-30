package cc.kenai.noti

import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import cc.kenai.noti.events.RulesChanged
import cc.kenai.noti.events.ServiceEnableChangedEvent
import cc.kenai.noti.model.NotiType
import cc.kenai.noti.model.NotificationFilter
import cc.kenai.noti.model.RulesFactory
import cc.kenai.noti.model.SuggestManager
import cc.kenai.noti.utils.ConfigHelper
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
                NotiHelperUtil.cancelAlarm(context)
                if (mKeyMap.size > 0) {
                    for ((_, v) in mKeyMap) {
                        if (v.needRing) {
                            NotiHelperUtil.playAlarm(mContext)
                            return
                        }
                    }
                }
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

        if (mEnable && notiType != null) {
            val key = sbn2Key(sbn)
            if (!mKeyMap.contains(key)) {
                mKeyMap.put(key, notiType)
            }
            notifyIfNeed(key)
        } else {
            SuggestManager.add(NotificationFilter.buildRule(sbn))
        }
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
        start(application)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    fun start(a: Application) {
        mContext = a
        mEnable = (mContext as XApplication).serviceEnable
        NotificationFilter.setupRules(RulesFactory.loadRules(mContext))
        val filter = IntentFilter(NotiHelperUtil.ACTION_CANCEL_RING)
        filter.addAction(NotiHelperUtil.ACTION_ALARM)
        mContext.registerReceiver(mReceiver, filter)
        RxBus.get().register(this)
    }

    fun stop() {
        cancelNotifyForce()

        RxBus.get().unregister(this)
        mContext.unregisterReceiver(mReceiver)
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


    private lateinit var mContext: Context

    @Subscribe
    fun onSaveRules(event: RulesChanged) {
        val sb = StringBuilder()
        event.rule.forEach {
            sb.append(RulesFactory.rule2json(it)).append("\n")
        }
        ConfigHelper.preferences.edit().putString("rules", sb.toString()).apply()
        NotificationFilter.setupRules(event.rule)
    }

    private var mEnable = false

    @Subscribe
    fun onServiceEnableChanged(event: ServiceEnableChangedEvent) {
        mEnable = event.enable
        if (!mEnable) {
            cancelNotifyForce()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand");
        return Service.START_STICKY
    }

    private fun sbn2Key(sbn: StatusBarNotification): String {
        return sbn.key
    }

    private var mSubscribe: Disposable? = null
    private var mDelayNotiSubscribe: Disposable? = null

    private fun notifyIfNeed(key: String) {
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
                            for ((_, v) in mKeyMap) {
                                if (v.needLoop) {
                                    NotiHelperUtil.ring(mContext)
                                    return@subscribe
                                }
                            }
                        }
                        cancelNotifyForce()
                    })
        }

        if (needNoti) {
            log("start notify")
            //非静音状态下立即提醒，否则延时30s提醒
            val audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.ringerMode) {
                NotiHelperUtil.vibrate(mContext)
                Observable.timer(30, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            mKeyMap.keys.find { it == key }?.let {
                                mKeyMap[it]?.let {
                                    if (it.needNoti && !it.needLoop) {
                                        NotiHelperUtil.ring(mContext)
                                    }
                                }
                            }
                        })
            } else {
                NotiHelperUtil.ring(mContext)
            }
        }

        if (needRing && !NotiHelperUtil.existAlarm()) {
            log("start ring")
            NotiHelperUtil.alarm(mContext)
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

        NotiHelperUtil.cancelRing(mContext)
        NotiHelperUtil.cancelAlarm(mContext)
        NotiHelperUtil.stopPlayAlarm(mContext)
    }
}