package cc.kenai.noti

import android.app.Application
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.model.Rule

class XApplication : Application() {
    var xServiceCount = 0L

    val historyNotification = ArrayList<Rule>()

    override fun onCreate() {
        super.onCreate()
        IconCache.init(this)
    }
}