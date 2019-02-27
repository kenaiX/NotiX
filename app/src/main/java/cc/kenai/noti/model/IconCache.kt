package cc.kenai.noti.model

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import cc.kenai.noti.R

object IconCache {
    private var mConnectIndex = 0
    private lateinit var mContext: Context
    private lateinit var mDefaultDrawable: Drawable
    private val mIconMap = HashMap<String, AppInfo>()

    private val mLock = Any()

    fun init(context: Context) {
        mContext = context
        mDefaultDrawable = context.resources.getDrawable(R.mipmap.ic_launcher)
    }

    fun connect(context: Context, callback: Runnable? = null) {
        mConnectIndex++
        Thread(Runnable {
            loadAllApps()
            if (callback != null) {
                callback.run()
            }
        }).start()
    }

    fun release() {
        mConnectIndex--
        if (mConnectIndex < 0) {
            throw RuntimeException("IconCache release error")
        }
        synchronized(mLock) {
            mIconMap.clear()
        }
    }

    fun getAppInfo(pkg: String): AppInfo? {
        synchronized(mLock) {
            return mIconMap.get(pkg)
        }
    }

    fun copyAllAppInfo(): ArrayList<AppInfo> {
        synchronized(mLock) {
            val result = ArrayList<AppInfo>((mIconMap.size * 1.6f).toInt())
            for ((_, v) in mIconMap) {
                result.add(AppInfo(v.pkg, v.name, v.icon))
            }
            return result
        }
    }

    fun loadAllApps() {
        val manager = mContext.packageManager
        synchronized(mLock) {
            mIconMap.put(NotificationFilter.ANY, AppInfo(NotificationFilter.ANY, "任意", ColorDrawable(Color.TRANSPARENT)))
            manager.getInstalledApplications(0).forEach {
                mIconMap.put(it.packageName,
                        AppInfo(it.packageName, it.loadLabel(manager).toString(),
                                it.loadIcon(manager) ?: mDefaultDrawable))
            }
        }
    }

    class AppInfo(val pkg: String, val name: String, val icon: Drawable)

}