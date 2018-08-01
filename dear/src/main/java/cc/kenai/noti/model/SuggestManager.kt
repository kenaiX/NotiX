package cc.kenai.noti.model

import java.util.*
import kotlin.collections.ArrayList

object SuggestManager {
    private val mHistoryNotification = LinkedList<Rule>()

    fun clear() {
        synchronized(mHistoryNotification) {
            mHistoryNotification.clear()
        }
    }

    fun add(rule: Rule) {
        synchronized(mHistoryNotification) {
            val b = mHistoryNotification.find {
                rule.pkg_limit == it.pkg_limit && rule.title == it.title && rule.text == it.text
            }
            if (b == null) {
                mHistoryNotification.addFirst(rule)
                if (mHistoryNotification.size > 100) {
                    mHistoryNotification.removeLast()
                }
            }
        }
    }

    fun getAll(): List<Rule> = ArrayList(mHistoryNotification)
}