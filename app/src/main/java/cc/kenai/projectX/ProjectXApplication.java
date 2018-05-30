package cc.kenai.projectX;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.flyme.systemuitools.launcher.RedPointManager;

import java.lang.reflect.Method;

public class ProjectXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RedPointManager redPointManager = RedPointManager.getInstance();
        try {
            Method method = redPointManager.getClass().getDeclaredMethod("dealWithNotificationChange", Boolean.TYPE, String.class, String.class);
            method.setAccessible(true);
            method.invoke(redPointManager, false, "com.android.settings", "com.android.settings1");
            method.invoke(redPointManager, false, "com.android.settings", "com.android.settings2");
            method.invoke(redPointManager, false, "com.android.alarmclock", "com.android.settings3");
            method.invoke(redPointManager, false, "com.android.alarmclock", "com.android.settings4");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //forTest
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String list = intent.getStringExtra("list");

                RedPointManager redPointManager = RedPointManager.getInstance();
                try {
                    String[] split = list.split(";");
                    Method method = redPointManager.getClass().getDeclaredMethod("dealWithNotificationChange", Boolean.TYPE, String.class, String.class);
                    method.setAccessible(true);
                    for (String s : split) {
                        method.invoke(redPointManager, false, s, s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(receiver,new IntentFilter("test.redpoint"));

    }
}
