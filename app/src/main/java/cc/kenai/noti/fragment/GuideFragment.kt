package cc.kenai.noti.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatSeekBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import cc.kenai.noti.R
import cc.kenai.noti.events.ServiceEnableChangedEvent
import cc.kenai.noti.getXApplication
import cc.kenai.noti.utils.ConfigHelper
import cc.kenai.noti.utils.NotiHelperUtil
import com.hwangjr.rxbus.RxBus


class GuideFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.guide_settings, container, false)
        view.findViewById<Button>(R.id.button_check).also {
            it.setText(if (getXApplication().serviceEnable) "关闭服务" else "打开服务")
            it.setOnClickListener {
                val to = !getXApplication().serviceEnable
                RxBus.get().post(ServiceEnableChangedEvent(to))
                (it as Button).setText(if (to) "关闭服务" else "打开服务")
            }
        }
        view.findViewById<Button>(R.id.button_p).also {
            it.setText("授予权限")
            it.setOnClickListener {
                val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
                intent.putExtra("packageName", activity.getPackageName())
                activity.startActivity(intent)
            }
        }
        view.findViewById<Button>(R.id.button_change_ring).run {
            setText("选择提示音")
            setOnClickListener {
                ConfigHelper.changeRingSound(activity)
            }
        }

        view.findViewById<AppCompatSeekBar>(R.id.seekbar_v).also {
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    ConfigHelper.volume = seekBar.progress / 100f
                    NotiHelperUtil.playNoti(seekBar.context)
                }
            })
            it.setProgress((ConfigHelper.volume * 100).toInt())
            it.max = 100
        }

        view.findViewById<Button>(R.id.button_test).also {
            it.setOnClickListener({ NotiHelperUtil.testNoti(context) })
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
}