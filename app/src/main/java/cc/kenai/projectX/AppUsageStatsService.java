package cc.kenai.projectX;

import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

public class AppUsageStatsService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, SystemClock.currentThreadTimeMillis() - 60 * 60 * 1000, SystemClock.currentThreadTimeMillis());

        for(UsageStats info: usageStats){
            Log.e("@@@@",info.getPackageName()+" time:"+info.getTotalTimeInForeground());
        }

    }
}
