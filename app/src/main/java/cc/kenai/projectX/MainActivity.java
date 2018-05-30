package cc.kenai.projectX;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, System.currentTimeMillis());

        if (usageStats.isEmpty()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        for (UsageStats info : usageStats) {
            Log.e("@@@@", info.getPackageName() + " time:" + info.getTotalTimeInForeground());
        }


        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        IntentFilter it = new IntentFilter();
        it.addAction(Intent.ACTION_SCREEN_OFF);
        it.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver,it);
    }

    ActivityManager am;

    class RecordThread extends Thread {
        boolean alive = true;
        @Override
        public void run() {
            while (alive) {
                List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                if (runningTasks.size() > 0) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    Log.e("@@@@", "now running : " + runningTaskInfo.topActivity.getPackageName());
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    RecordThread t;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if(recordAlive) {
                    recordAlive = false;
                    if(t!=null) {
                        t.alive = false;
                        t = null;
                    }
                }
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                if (!recordAlive) {
                    recordAlive = true;
                    if(t==null) {
                        t = new RecordThread();
                        t.start();
                    }
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    boolean recordAlive = false;
}
