package cc.kenai.dear.model

import android.app.Notification
import android.service.notification.StatusBarNotification

class NotificationFilter {

    private val mFilterList = arrayListOf<String>("亲爱哒")

    fun needNoti(sbn: StatusBarNotification): Boolean {
        if (sbn.packageName == "com.tencent.mm") {
            val notification = sbn.notification
            val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
            if (mFilterList.contains(title))
                return true
        } else if (sbn.packageName == "com.huawaii.notifications") {
            return true
        }
        return false
    }
}