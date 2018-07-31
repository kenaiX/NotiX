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
            mHistoryNotification.addFirst(rule)
            if (mHistoryNotification.size > 100) {
                mHistoryNotification.removeLast()
            }
        }
    }

    fun getAll(): List<Rule> = ArrayList(mHistoryNotification)
}