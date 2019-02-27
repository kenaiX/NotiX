package cc.kenai.noti

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cc.kenai.noti.events.RuleCommit
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.model.NotificationFilter
import cc.kenai.noti.model.RulesFactory
import cc.kenai.noti.view.NotiTypeViewGroup
import com.hwangjr.rxbus.RxBus
import com.meizu.flyme.launcher.IExternalService
import java.text.Collator
import java.util.*


class RuleEditActivity : AppCompatActivity() {
    lateinit var mPkgImageView: ImageView
    lateinit var mTitleEditText: AppCompatEditText
    lateinit var mTextEditText: AppCompatEditText
    lateinit var mCommitButton: Button
    lateinit var mTypeGroup: NotiTypeViewGroup

    lateinit var mPkgLimit: String
    private val mAppsUsedRecord = HashMap<String, Int>()//用来给应用排序

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rule_edit)
        val any = intent.extras["rule"] as String
        val rule = RulesFactory.json2rule(any)
        val globalRules = NotificationFilter.copyRules()
        val isEdit = intent.getBooleanExtra("edit", false)
        mPkgLimit = rule.pkg_limit

        val parent = findViewById<View>(R.id.rule_edit)
        mPkgImageView = parent.findViewById(R.id.pkg)
        mTitleEditText = parent.findViewById(R.id.title)
        mTextEditText = parent.findViewById(R.id.text)
        mTypeGroup = parent.findViewById(R.id.notitype_group)
        mCommitButton = parent.findViewById(R.id.commit)

        updatePkg(mPkgLimit)
        mTitleEditText.setText(rule.title)
        mTextEditText.setText(rule.text)
        mTypeGroup.setup(rule.type)

        mPkgImageView.setOnClickListener {
            showPkgSelectPopWindow(it)
        }

        mCommitButton.setOnClickListener {

            rule.pkg_limit = mPkgLimit
            rule.title = mTitleEditText.text.toString()
            rule.text = mTextEditText.text.toString()
            rule.type = mTypeGroup.getType()

            //检查
            if (rule.title.isEmpty()) {
                rule.title = NotificationFilter.ANY
            }
            //检查
            if (rule.text.isEmpty()) {
                rule.text = NotificationFilter.ANY
            }

            val repeat = globalRules.find {
                it.pkg_limit == rule.pkg_limit
                        && it.title == rule.title
                        && it.text == rule.text
            }

            if (!isEdit && repeat != null) {
                Toast.makeText(baseContext, "存在重复规则", Toast.LENGTH_SHORT).show()
            } else {

                try {
                    Regex(rule.title)
                    Regex(rule.text)
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "错误规则", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                RxBus.get().post(RuleCommit(rule))
                finish()
            }
        }

        //从桌面拉取应用启动次数
        val it = Intent()
        it.component = ComponentName("com.meizu.flyme.launcher", "com.meizu.flyme.launcher.ExternalService")
        bindService(it, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val ss = IExternalService.Stub.asInterface(service)
                try {
                    val appsUsedRecord = ss.getAppsUsedRecord()
                    val pkgAndTimes = appsUsedRecord.split(";".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    for (temp in pkgAndTimes) {
                        if (!TextUtils.isEmpty(temp)) {
                            val temp2 = temp.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                            mAppsUsedRecord[temp2[0]] = Integer.valueOf(temp2[1])
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }, Service.BIND_AUTO_CREATE)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    fun onEditButtonClick(v: View) {
        when (v.id) {
            R.id.button_title -> mTitleEditText.setText(NotificationFilter.ANY)
            R.id.button_text -> mTextEditText.setText(NotificationFilter.ANY)
        }
    }

    private fun updatePkg(pkg: String) {
        mPkgLimit = pkg
        mPkgImageView.setImageDrawable(IconCache.getAppInfo(mPkgLimit)?.icon)
    }

    fun showPkgSelectPopWindow(v: View) {
        val pop = PopupWindow(resources.getDimensionPixelSize(R.dimen.popwindow_app_select_width), resources.getDimensionPixelSize(R.dimen.popwindow_app_select_height))
        val contentView = ListView(this)
        val padding = resources.getDimensionPixelSize(R.dimen.default_padding_large);
        contentView.setPadding(padding, padding, padding, padding)
        contentView.elevation = 20f
        contentView.setBackgroundColor(Color.WHITE)
        val apps = IconCache.copyAllAppInfo()
        apps.sortBy {
            it.pkg
        }
        val collator = Collator.getInstance(Locale.CHINESE)
        apps.sortWith(Comparator { o1, o2 ->
            if (o1.pkg == mPkgLimit) {
                return@Comparator -1
            }
            if (o2.pkg == mPkgLimit) {
                return@Comparator 1
            }
            if (o1.pkg == NotificationFilter.ANY) {
                return@Comparator -1
            }
            if (o2.pkg == NotificationFilter.ANY) {
                return@Comparator 1
            }

            val times1 = mAppsUsedRecord.get(o1.pkg)
            val times2 = mAppsUsedRecord.get(o2.pkg)
            if (times1 == null && times2 == null) {
                collator.getCollationKey(o1.name).compareTo(collator.getCollationKey(o2.name))
            } else if (times1 == null) {
                1
            } else if (times2 == null) {
                -1
            } else {
                times2 - times1
            }
        })
        val adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val item = LayoutInflater.from(parent.context).inflate(R.layout.list_item_appinfo, parent, false)
                item.setOnClickListener() {
                    updatePkg((it.tag as IconCache.AppInfo).pkg)
                    pop.dismiss()
                }
                item.tag = apps[position]
                item.findViewById<ImageView>(R.id.pkg).setImageDrawable(apps[position]!!.icon)
                item.findViewById<TextView>(R.id.name).setText(apps[position]!!.name)
                return item
            }

            override fun getItem(position: Int): Any = apps[position]!!

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getCount(): Int = apps.size
        }
        contentView.adapter = adapter

        pop.contentView = contentView
        pop.setOutsideTouchable(true);
        pop.isFocusable = true
        pop.showAsDropDown(v)
    }
}