package com.flyme.systemuitools.gamemode.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flyme.systemuitools.gamemode.events.GameEvents;
import com.hwangjr.rxbus.RxBus;
import com.meizu.gamecenter.MzPluginPlatform;
import com.meizu.gamecenter.config.MzPluginEventCallback;
import com.qihoo360.replugin.model.PluginInfo;

public class GameSdkUtil {

    static final String TAG = "GameSdkUtil";

    public static void onApplicationAttach(Application context){
        try {
            MzPluginPlatform.attachBaseContext(context, new MzPluginEventCallback(context) {
                @Override
                public void onInstallPluginSucceed(PluginInfo info) {
                    super.onInstallPluginSucceed(info);
                    //插件安装成功处理,收到这个回调就可以通知游戏助手界面重新加载福利的View
                    Log.i(TAG, "onInstallPluginSucceed");
                    RxBus.get().post(new GameEvents.PluginInstalled(true));
                }

                @Override
                public void onInstallPluginFailed(String path, InstallResult code) {
                    super.onInstallPluginFailed(path, code);
                    //插件安装失败处理
                    Log.i(TAG, "onInstallPluginFailed:" + code);
                    RxBus.get().post(new GameEvents.PluginInstalled(false));
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void onApplicationCreate(){
        try {
            MzPluginPlatform.onCreate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void onApplicationLowMemory(){
        try {
            MzPluginPlatform.onLowMemory();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void onApplicationTrimMemory(int level){
        try {
            MzPluginPlatform.onTrimMemory(level);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void onApplicationConfigurationChanged(Configuration config){
        try {
            MzPluginPlatform.onConfigurationChanged(config);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void preparePluginIfneed(Context context) {
        if (!MzPluginPlatform.isPluginInstalled()) { //先判断插件有没有安装
            MzPluginPlatform.install(context);
        }
    }

    public static boolean isPluginInstalled() {
        return MzPluginPlatform.isPluginInstalled();
    }

    /**
     * @param parent  加载福利的窗口
     * @param subView 传给福利一个全屏展示的窗口
     */
    public static void showGameView(ViewGroup parent, ViewGroup subView, String pkg) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("packageName", pkg); //当前游戏包名,需要游戏助手传过来
            View view = MzPluginPlatform.getGameAssistantView(parent.getContext().getApplicationContext(), subView, bundle);
            if (view != null) {
                //游戏助手把福利的view add 到相应窗口
                parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
