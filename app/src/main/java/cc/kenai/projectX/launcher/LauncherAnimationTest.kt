package cc.kenai.projectX.launcher

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.LinearLayout
import com.meizu.flyme.animatorservice.IMzAnimatorService

class LauncherAnimationTest : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buttonLock = Button(this)
        val buttonUnLock = Button(this)

        buttonLock.text = "调用锁屏动画"
        buttonUnLock.text = "调用解锁动画"

        val list = LinearLayout(this);
        list.addView(buttonLock)
        list.addView(buttonUnLock)

        setContentView(list)

        buttonLock.setOnClickListener({lock((true))})
        buttonUnLock.setOnClickListener({lock((false))})
    }

    fun lock(islock: Boolean) {
        val it = Intent()
        it.component = ComponentName("com.meizu.flyme.launcher", "com.meizu.flyme.animatorservice.MzAnimatorService")
        bindService(it, object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val asInterface = IMzAnimatorService.Stub.asInterface(service)
                if (islock) {
                    asInterface.hideLauncher()
                } else {
                    asInterface.showLauncherAnimation()
                }
                unbindService(this)
            }
        }, 0);
    }


}
