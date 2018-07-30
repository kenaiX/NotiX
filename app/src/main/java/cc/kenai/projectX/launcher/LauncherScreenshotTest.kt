package cc.kenai.projectX.launcher

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.meizu.flyme.launcher.IExternalCallback
import com.meizu.flyme.launcher.IExternalService

class LauncherScreenshotTest : Activity() {

    lateinit var img:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buttonLock = Button(this)
        val buttonUnLock = Button(this)

        buttonLock.text = "调用锁屏动画"
        buttonUnLock.text = "调用解锁动画"

        val list = LinearLayout(this);
        list.orientation= LinearLayout.VERTICAL
        list.addView(buttonLock)
        list.addView(buttonUnLock)

        img = ImageView(this)
        img.setBackgroundColor(Color.WHITE)
        list.addView(img)

        setContentView(list)

        buttonLock.setOnClickListener({lock((true))})
        buttonUnLock.setOnClickListener({lock((false))})


        val it = Intent("com.flyme.systemuitools.launcher.TOGGLE_SHORTCUT_BADGE")
        it.putExtra("extra_pkg","com.huawaii.notifications")
        it.putExtra("extra_action","show")
        sendBroadcast(it)
    }

    var mCallback =  object :IExternalCallback.Stub(){
        override fun onLauncherResume() {
            Log.e("@@@@","resume")
        }
    };

    fun lock(islock: Boolean) {
        val it = Intent()
        it.component = ComponentName("com.meizu.flyme.launcher", "com.meizu.flyme.launcher.ExternalService")
        bindService(it, object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val asInterface = IExternalService.Stub.asInterface(service)

                //测试截图
                //下面的参数注意判空
                val bundle = asInterface.takeScreenshot();
                val bitmap = bundle.getParcelable("screenshot") as Bitmap;//桌面截图
                val position = bundle.getParcelable("position") as Rect;//图标返回的位置
                val icon = bundle.getParcelable("icon") as Bitmap;//图标
                val isFolder = bundle.getBoolean("is_folder", false);//当前是否文件夹打开状态

                //测试代码
                val paint = Paint()
                paint.color = Color.WHITE
                paint.setStyle(Paint.Style.STROKE);
                paint.strokeWidth = 3f
                val canvas = Canvas(bitmap)
                canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)//绘制外边框
                canvas.drawRect(position, paint)//绘制图标位置
                canvas.drawBitmap(icon, Rect(0, 0, icon.width, icon.height), position, paint)//绘制被扣掉的图标
                img.setImageBitmap(bitmap)


                //测试桌面缩放
                asInterface.snapHome(true)
                asInterface.scaleHome(0.5f)

                asInterface.setExternalCallback(mCallback)

                unbindService(this)
            }
        }, Service.BIND_AUTO_CREATE);
    }


}
