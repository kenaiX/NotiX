package com.flyme.systemuitools.gamemode.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public abstract class BatteryObserver {

    public abstract void onBatteryChange(int level, String time, boolean isCharging);

    Context mContext;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//            int status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            onBatteryChange(level, status == BatteryManager.BATTERY_STATUS_CHARGING ? "充电中" : "未充电", status == BatteryManager.BATTERY_STATUS_CHARGING);
        }
    };

    public void init(Context context) {
        mContext = context.getApplicationContext();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        /*filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);*/
        mContext.registerReceiver(receiver, filter);
    }

    public void reset() {
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext = null;
    }
}
