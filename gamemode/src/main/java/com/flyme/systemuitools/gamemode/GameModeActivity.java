package com.flyme.systemuitools.gamemode;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.flyme.systemuitools.SystemUIToolsNotificationListenerService;
import com.flyme.systemuitools.gamemode.model.AppInfo;
import com.flyme.systemuitools.gamemode.utils.FloatWindowWhiteListHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * For test
 */
public class GameModeActivity extends Activity {
    ProViewControler proViewControler;

    private LauncherApps mLauncherApps;
    private PackageManager mPkgManager;
    private UserManager mUserManager;

    private ProViewControler.CallBack mCallback = new ProViewControler.CallBack() {
        @Override
        public List<AppInfo> loadAllApps() {
            String currentPkg = getGamePkg();
            List<AppInfo> list = new ArrayList<>();
            final List<UserHandle> profiles = mUserManager.getUserProfiles();
            for (UserHandle user : profiles) {
                if (user.equals(Process.myUserHandle())) {
                    List<LauncherActivityInfo> activityList = mLauncherApps.getActivityList(null, user);
                    outer:
                    for (LauncherActivityInfo info : activityList) {
                        String packageName = info.getComponentName().getPackageName();
                        if (TextUtils.equals(currentPkg, packageName)
                                || !FloatWindowWhiteListHelper.inWhiteList(packageName)) {
                            continue outer;
                        }
                        list.add(loadAppInfo(info));
                    }
                }
            }
            return list;
        }

        @Override
        public AppInfo loadApps(String pkg, String name, int userid) {
            //todo
            List<LauncherActivityInfo> activityList = mLauncherApps.getActivityList(pkg, Process.myUserHandle());
            if (activityList.size() > 0) {
                for (LauncherActivityInfo info : activityList) {
                    if (info.getComponentName().getClassName().equals(name)) {
                        return loadAppInfo(info);
                    }
                }
            }
            return null;
        }

        @Override
        public void startApps(AppInfo info) {
            //todo
            mLauncherApps.startMainActivity(info.getInfo().getComponentName(), Process.myUserHandle(), null, null);
        }

        @Override
        public List<StatusBarNotification> getNotifications() {
            StatusBarNotification[] notifications = SystemUIToolsNotificationListenerService.getInstance().getActiveNotifications();
            List<StatusBarNotification> list = new ArrayList<>();
            if (notifications != null && notifications.length > 0) {
                for (StatusBarNotification sn : notifications) {
                    list.add(sn);
                }
            }
            return list;
        }

        @Override
        public String getGamePkg() {
            ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
            try {
                List<ActivityManager.RecentTaskInfo> taskInfos = am.getRecentTasks(1, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return taskInfos.get(0).topActivity.getPackageName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onShowChange(boolean isShow, boolean showPanadaImmediately) {

        }

        @Override
        public int getWelfareNum() {
            return 1;
        }

        @Override
        public String computeBatteryTimeRemaining() {
            return "3分钟";
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLauncherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        mPkgManager = getPackageManager();
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);


        proViewControler = new ProViewControler(this, mCallback);
        proViewControler.toggleProViewShow(true);


        ImageView im = new ImageView(this);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proViewControler.toggleProViewShow(!proViewControler.isShow());
            }
        });
        setContentView(im);

        im.setBackgroundColor(Color.rgb(235, 235, 235));
    }


    private AppInfo loadAppInfo(LauncherActivityInfo info) {
        try {
            String pkg = info.getComponentName().getPackageName();
            PackageInfo pkgInfo = mPkgManager.getPackageInfo(pkg, 0);
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            Drawable d = null;
            if (appInfo != null) {
                d = appInfo.loadIcon(mPkgManager);
            }
            return new AppInfo(d, info.getLabel(), info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        proViewControler.toggleProViewShow(false);
    }
}
