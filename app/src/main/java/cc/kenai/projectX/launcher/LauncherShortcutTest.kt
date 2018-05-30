package cc.kenai.projectX.launcher

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import cc.kenai.projectX.R

class LauncherShortcutTest : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shortcut_test)

    }

    fun createShortcut(view:View){
        createShortcut(false,"test1", "test1",ComponentName(packageName,"cc.kenai.projectX.launcher.LauncherShortcutTest"),R.mipmap.ic_launcher);
    }

    fun createShortcut2(view:View){
        createShortcut(true,"test1","test2", ComponentName(packageName,"cc.kenai.projectX.launcher.LauncherShortcutTest"),R.mipmap.ic_launcher);
    }

    private fun createShortcut(isUpdate:Boolean,oldTitle:String,title:String, componentName: ComponentName, icon:Int) {
        // 安装的Intent
        val shortcut = Intent("com.meizu.flyme.launcher.action.INSTALL_SHORTCUT")
        // 快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title)
        // 快捷图标是否允许重复
        shortcut.putExtra("duplicate", false)

        //创建快捷方式的intent
        val shortcutIntent = Intent(Intent.ACTION_MAIN)
        shortcutIntent.setComponent(componentName)
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)


        if(isUpdate){
            shortcut.putExtra("flyme.intent.extra.shortcut.UPDATE", true)
            shortcut.putExtra("flyme.intent.extra.shortcut.OLD_NAME", oldTitle)

            val shortcutIntent = Intent(Intent.ACTION_MAIN)
            shortcutIntent.setComponent(componentName)
            shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            shortcut.putExtra("flyme.intent.extra.shortcut.OLD_INTENT", shortcutIntent)
        }

        // 快捷图标  TODO 图标处理   将网络图片下载并创建快捷方式图标
        val iconRes = Intent.ShortcutIconResource.fromContext(this, icon)
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)

        // 发送广播
        sendBroadcast(shortcut)
    }
}
