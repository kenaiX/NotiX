package com.flyme.systemuitools.gamemode.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.flyme.systemuitools.gamemode.ProViewControler;
import com.flyme.systemuitools.gamemode.model.AppInfo;

import java.util.ArrayList;

public class QuickAppsHelper {
    public static ArrayList<ProViewControler.QuickAppsRecord> getList(Context context) {
        ArrayList<ProViewControler.QuickAppsRecord> list = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_mode", Context.MODE_PRIVATE);
        String saveString = sharedPreferences.getString("quick_apps_list", null);
        if (saveString == null) {
            list.add(new ProViewControler.QuickAppsRecord(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"), 0));
            list.add(new ProViewControler.QuickAppsRecord(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity"), 0));
        } else {
            String[] split = saveString.split(";");
            for (String s : split) {
                if (!TextUtils.isEmpty(s)) {
                    list.add(ProViewControler.QuickAppsRecord.fromString(s));
                }
            }
        }
        return list;
    }

    public static void save(Context context, AppInfo[] infos) {
        StringBuilder sb = new StringBuilder();
        for (AppInfo info : infos) {
            if (info != null) {
                sb.append(ProViewControler.QuickAppsRecord.AppInfo2String(info)).append(";");
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("game_mode", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("quick_apps_list", sb.toString()).apply();
    }
}
