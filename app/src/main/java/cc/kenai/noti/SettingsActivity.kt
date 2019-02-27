package cc.kenai.noti

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import cc.kenai.noti.fragment.DemoFragment
import cc.kenai.noti.fragment.FilterSettingsFragment
import cc.kenai.noti.fragment.GuideFragment
import cc.kenai.noti.model.IconCache
import cc.kenai.noti.utils.ConfigHelper
import java.lang.ref.WeakReference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = WeakReference<SettingsActivity>(this)
        IconCache.connect(this, object : Runnable {
            override fun run() {
                activity.get()?.runOnUiThread { show() }
            }
        })
        setContentView(R.layout.welcome_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        IconCache.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == ConfigHelper.RESULT_ACTION_CHANGE_RING) {
            val pickedUri = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            //将我们选择的铃声设置成为默认
            pickedUri?.run { ConfigHelper.onRingChangeReturn(this) }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun show() {
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val fm = supportFragmentManager
        viewPager.adapter = object : FragmentStatePagerAdapter(fm) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    return GuideFragment()
                } else if (position == 1) {
                    return FilterSettingsFragment()
                } else {
                    return DemoFragment()
                }
            }

            override fun getCount(): Int = 3

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> "状态"
                    1 -> "规则"
                    2 -> "建议"
                    else -> ""
                }
            }
        }
        viewPager.currentItem = 1

        findViewById<TabLayout>(R.id.tab).also {
            it.setupWithViewPager(viewPager)
        }
    }
}
