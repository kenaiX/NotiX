package cc.kenai.noti.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cc.kenai.noti.R
import cc.kenai.noti.events.ServiceEnableChangedEvent
import cc.kenai.noti.getXApplication
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