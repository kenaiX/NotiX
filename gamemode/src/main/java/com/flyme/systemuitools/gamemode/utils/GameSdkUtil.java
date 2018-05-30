package com.flyme.systemuitools.gamemode.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.meizu.gamecenter.MzPluginPlatform;

public class GameSdkUtil {
    public static void preparePluginIfneed(Context context) {
        if (!MzPluginPlatform.isPluginInstalled()) { //先判断插件有没有安装
            MzPluginPlatform.install(context);
        }
    }

    public static boolean isPluginInstalled() {
        return MzPluginPlatform.isPluginInstalled();
    }

    public static void showGameView(ViewGroup parent, String pkg) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("packageName", pkg); //当前游戏包名,需要游戏助手传过来
            View view = MzPluginPlatform.getGameAssistantView(parent.getContext(), bundle);
            if (view != null) {
                //游戏助手把福利的view add 到相应窗口
                parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
