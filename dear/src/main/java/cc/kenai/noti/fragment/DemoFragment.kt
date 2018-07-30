package cc.kenai.noti.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import cc.kenai.noti.R
import cc.kenai.noti.RuleEditActivity
import cc.kenai.noti.XApplication
import cc.kenai.noti.events.RuleCommit
import cc.kenai.noti.events.RulesChanged
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.model.NotificationFilter
import cc.kenai.noti.model.Rule
import cc.kenai.noti.model.RulesFactory
import cc.kenai.noti.utils.NotiHelperUtil
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe

class DemoFragment : Fragment(), View.OnClickListener, View.OnLongClickListener {
    override fun onLongClick(v: View): Boolean {
        if (v.tag != null) {
            val tag = v.tag as Rule
            mAdapter.mRules.remove(tag)
            mAdapter.notifyDataSetChanged()
            mSavaRulesFlag = true
        }
        return true
    }

    var mEditRule: Rule? = null
    var mSavaRulesFlag = false;
    @Subscribe
    fun onRuleEdited(event: RuleCommit) {

    }

    override fun onClick(v: View) {
            val tag = v.tag as Rule
            mEditRule = tag
            val it = Intent(v.context, RuleEditActivity::class.java)
            it.putExtra("rule", RulesFactory.rule2json(tag))
            it.putExtra("add", true)
            v.context.startActivity(it)
    }

    lateinit var mAdapter: RulesAdapter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.demo_settings, container, false)
        view.findViewById<ListView>(R.id.list).also {
            mAdapter = RulesAdapter(this)
            mAdapter.mRules.addAll((activity.application as XApplication).historyNotification.filter {
                IconCache.getAppInfo(it.pkg_limit) != null
            })
            it.adapter = mAdapter
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        if (mSavaRulesFlag) {
            mSavaRulesFlag = false
            RxBus.get().post(RulesChanged(mAdapter.mRules.toTypedArray()))
        }
    }

    override fun onResume() {
        super.onResume()
        mEditRule = null
        if (mSavaRulesFlag) {
            mSavaRulesFlag = false
            RxBus.get().post(RulesChanged(mAdapter.mRules.toTypedArray()))
        }
    }

    class RulesAdapter(val mClickListener: DemoFragment) : BaseAdapter() {
        var mRules = ArrayList<Rule>()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var result: View
            if (convertView == null) {
                result = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rule, parent, false)
                result.setOnClickListener(mClickListener)
                result.setOnLongClickListener(mClickListener)
            } else {
                result = convertView
            }

            val rule = mRules[position]

            val appInfo = IconCache.getAppInfo(rule.pkg_limit)
            if (appInfo != null) {
                result.findViewById<ImageView>(R.id.pkg).setImageDrawable(appInfo.icon)
            } else {
                result.findViewById<ImageView>(R.id.pkg).setImageDrawable(null)
            }
            result.findViewById<TextView>(R.id.title).also {
                if (rule.title == NotificationFilter.ANY) {
                    it.setTextColor(Color.GREEN)
                    it.setText("任意")
                } else {
                    it.setTextColor(Color.GRAY)
                    it.setText(rule.title)
                }
            }
            result.findViewById<TextView>(R.id.text).also {
                if (rule.text == NotificationFilter.ANY) {
                    it.setTextColor(Color.GREEN)
                    it.setText("任意")
                } else {
                    it.setTextColor(Color.GRAY)
                    it.setText(rule.text)
                }
            }
            result.findViewById<TextView>(R.id.type).setText(rule.type)
            result.tag = rule

            return result
        }

        override fun getItem(position: Int): Any = mRules[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = mRules.size

    }
}