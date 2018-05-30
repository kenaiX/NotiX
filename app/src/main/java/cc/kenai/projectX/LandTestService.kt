package cc.kenai.projectX

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder

/**
 * Created by yujunqing on 2017/10/20.
 */
class LandTestService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!t.isAlive) {
            t.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    val t = Thread(Runnable {
        while (true) {
            Thread.sleep(2000)
            val it = Intent(this, Class.forName("cc.kenai.projectX.LandActivity"))
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    })
}