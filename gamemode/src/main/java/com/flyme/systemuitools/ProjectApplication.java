package com.flyme.systemuitools;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.flyme.systemuitools.gamemode.events.GameEvents;
import com.hwangjr.rxbus.RxBus;
import com.meizu.gamecenter.MzPluginPlatform;
import com.meizu.gamecenter.config.MzPluginEventCallback;
import com.qihoo360.replugin.model.PluginInfo;

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MzPluginPlatform.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MzPluginPlatform.attachBaseContext(this,new MzPluginEventCallback(base){
            @Override
            public void onInstallPluginSucceed(PluginInfo info) {
                super.onInstallPluginSucceed(info);
                //插件安装成功处理,收到这个回调就可以通知游戏助手界面重新加载福利的View
                Log.e("GameApplication","onInstallPluginSucceed");
                RxBus.get().post(new GameEvents.PluginInstalled(true));
            }

            @Override
            public void onInstallPluginFailed(String path, InstallResult code) {
                super.onInstallPluginFailed(path, code);
                //插件安装失败处理
                Log.e("GameApplication","onInstallPluginFailed:" +code);
                RxBus.get().post(new GameEvents.PluginInstalled(false));
            }
        });


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MzPluginPlatform.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        MzPluginPlatform.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        MzPluginPlatform.onConfigurationChanged(config);
    }
}
