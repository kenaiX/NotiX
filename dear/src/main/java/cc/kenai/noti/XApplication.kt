package cc.kenai.noti

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.app.Fragment
import cc.kenai.noti.events.ServiceEnableChangedEvent
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.utils.ConfigHelper
import cc.kenai.noti.utils.NotiHelperUtil
import cc.kenai.noti.utils.SystemProx
import com.flyme.systemui.recents.IRecentsRemoteService
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe

class XApplication : Application() {
    var serviceEnable = true

    override fun onCreate() {
        super.onCreate()
        ConfigHelper.init(this)
        IconCache.init(this)
        NotiHelperUtil.init(this)
        serviceEnable = getSharedPreferences("config", 0).getBoolean("enable", true)
        RxBus.get().register(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, WatchDogService::class.java))
        } else {
            startService(Intent(this, WatchDogService::class.java))
        }
        SystemProx.enableNotificationListenerService(this, true)

        lockActivityInRecents()
    }

    @Subscribe
    fun onServiceEnableChanged(event: ServiceEnableChangedEvent) {
        serviceEnable = event.enable
        getSharedPreferences("config", 0).edit().putBoolean("enable", serviceEnable)
        SystemProx.enableNotificationListenerService(this, serviceEnable)
    }

    private fun lockActivityInRecents() {
        val intent = Intent()
        intent.setClassName("com.android.systemui", "com.flyme.systemui.recents.RecentsRemoteService")
        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                try {
                    IRecentsRemoteService.Stub.asInterface(service).lockPkgTemporarily(packageName, 1 /*1:lock 0:unlock*/)
                    unbindService(this)//这里不是必须，应用自己视情况断开连接
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
            override fun onServiceDisconnected(name: ComponentName) {

            }
        }, Context.BIND_AUTO_CREATE)
    }
}

fun Fragment.getXApplication() = activity.application as XApplication