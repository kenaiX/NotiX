package cc.kenai.noti

import android.app.Application
import cc.kenai.noti.model.IconCache

class XApplication : Application() {
    var xServiceCount = 0L

    override fun onCreate() {
        super.onCreate()
        IconCache.init(this)
    }
}