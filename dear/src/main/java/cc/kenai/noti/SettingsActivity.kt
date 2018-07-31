package cc.kenai.noti

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

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IconCache.connect(this)

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
                return when(position){
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

    override fun onDestroy() {
        super.onDestroy()
        IconCache.release()
    }
}
