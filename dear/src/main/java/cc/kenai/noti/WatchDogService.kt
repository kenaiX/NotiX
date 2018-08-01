package cc.kenai.noti

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cc.kenai.noti.utils.NotiHelperUtil

class WatchDogService : Service(){
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1,NotiHelperUtil.buildForgroundNoti(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}