package cc.kenai.noti

import android.app.Application
import android.support.v4.app.Fragment
import cc.kenai.noti.events.ServiceEnableChangedEvent
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.utils.SystemProx
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe

class XApplication : Application() {
    var serviceEnable = true

    override fun onCreate() {
        super.onCreate()
        IconCache.init(this)
        serviceEnable = getSharedPreferences("config", 0).getBoolean("enable", true)
        RxBus.get().register(this)


        SystemProx.enableNotificationListenerService(this,true)
    }

    @Subscribe
    fun onServiceEnableChanged(event: ServiceEnableChangedEvent) {
        serviceEnable = event.enable
        getSharedPreferences("config", 0).edit().putBoolean("enable", serviceEnable)
        SystemProx.enableNotificationListenerService(this,serviceEnable)
    }
}

fun Fragment.getXApplication()=activity.application as XApplication