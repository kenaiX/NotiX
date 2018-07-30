package cc.kenai.noti.model

import android.app.Notification
import android.service.notification.StatusBarNotification

object NotificationFilter {
    const val ANY = ".*"

    private var mRules: Array<Rule> = emptyArray()
    private var mRulesRex: Array<RuleRex?> = emptyArray()

    fun setupRules(rules: Array<Rule>) {
        mRules = rules;
        mRulesRex = arrayOfNulls(mRules.size)
        var i = 0
        for (rule in mRules) {
            mRulesRex[i++] = RuleRex(Regex(rule.title), Regex(rule.text), Regex(rule.pkg_limit), rule.type)
        }
    }

    fun buildRule(sbn: StatusBarNotification): Rule {
        val notification = sbn.notification
        return Rule(notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString(),
                notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString(),
                sbn.packageName, "");
    }

    fun needNoti(sbn: StatusBarNotification): Boolean {
        val temp = buildRule(sbn);
        val result = mRulesRex.find {
            temp.pkg_limit.contains(it!!.pkg_limit) &&
                    temp.title.contains(it.title) &&
                    temp.text.contains(it.text)
            /*(it.pkg_limit == ANY || temp.pkg_limit == it.pkg_limit) &&
                    (it.title == ANY || temp.title == it.title) &&
                    (it.text == ANY || temp.text == it.text)*/
        }
        if (result != null) {
            return true
        }
        return false
    }

    fun copyRules(): Array<Rule> {
        return mRules.copyOf()
    }
}